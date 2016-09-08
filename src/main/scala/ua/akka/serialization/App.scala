package ua.akka.serialization

import akka.actor._
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.unmarshalling._

import scala.concurrent.{ExecutionContext, Future}

class TestSprayApiService()(implicit val context: ActorContext) {

  implicit lazy val ec: ExecutionContext = context.dispatcher

  val logRequest: HttpRequest => HttpRequest = { r =>
    println(s"API REQUEST: Method ${r.method} Endpoint ${r.uri} with Content:\n${r.entity.data.asString}")
    r
  }

  val logResponse: HttpResponse => HttpResponse = { r =>
    println(s"API RESPONSE:\n${r.entity.data.asString}")
    r
  }

  def pipeline = (
    logRequest
    ~> sendReceive
    ~> logResponse
    ~> unmarshal[String]
  )

//
//  def pipeline[cl: FromResponseUnmarshaller]() = (
//    logRequest
//    ~> sendReceive
//    ~> logResponse
//    ~> unmarshal[cl]
//  )

  def get(url: String): Future[String] = {
//    pipeline[String]() {
    pipeline {
      Get(url)
    }
  }

}

object SprayApiServiceActor {
  def name = "SprayApiService"
  def props = FromConfig.props(Props[SprayApiServiceActor])
  def router()(implicit system: ActorSystem) = system.actorSelection(s"/user/${ServiceSupervisorActor.name}/$name")
}

class SprayApiServiceActor extends Actor {
  import context._

  val service: TestSprayApiService = new TestSprayApiService()

  // 'def' will fail
  // def service: TestSprayApiService = new TestSprayApiService()

  // (String => String) will fail too
  // def service: (String => Future[String]) = new TestSprayApiService().get

  override def receive: Receive = {
    case url: String => service.get(url).onComplete(x => println(s"$self : $x"))
//    case url: String => service(url).onComplete(x => println(s"$self : $x"))
  }
}

object ServiceSupervisorActor {
  def name = "ServiceSupervisorActor"
  def props = Props[ServiceSupervisorActor]
}

class ServiceSupervisorActor extends Actor {
  import context._

  context.actorOf(SprayApiServiceActor.props, SprayApiServiceActor.name)

  val router = SprayApiServiceActor.router()

  import scala.concurrent.duration._
  system.scheduler.schedule(0.second, 5.seconds, self, "http://google.com")

  override def receive: Actor.Receive = {
    case url: String => router ! url
  }
}

object SprayApiServiceSeedNodeApp extends App {

  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=2551"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  system.actorOf(ServiceSupervisorActor.props, ServiceSupervisorActor.name)

  System.in.read
}

object SprayApiServiceNodeApp extends App {
  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  system.actorOf(ServiceSupervisorActor.props, ServiceSupervisorActor.name)


  System.in.read
}

object SprayApiServiceFailedApp extends App {
  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")

    // ENABLE SERIALIZE CHECK
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-messages = on"))
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-creators = on"))

    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  system.actorOf(ServiceSupervisorActor.props, ServiceSupervisorActor.name)

  System.in.read
}
