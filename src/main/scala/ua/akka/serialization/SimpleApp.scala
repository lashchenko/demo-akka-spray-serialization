package ua.akka.serialization

import akka.actor.{ActorContext, Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import spray.client.pipelining._

import scala.concurrent.{ExecutionContext, Future}

class TestClass {
  // THIS IS NOT SERIALIZABLE CLASS.
  def get(request: String): Future[String] = Future.successful("TEST: " + request)
}

object TestActor {
  def props() = Props(classOf[TestActor])
  def name = "TestActor"
}

class TestActor extends Actor {
  import context._

  def service: (String => Future[String]) = new TestClass().get

  override def receive: Receive = {
    case url: String => service(url).onComplete(x => println(s"$self : $x"))
  }
}

object SimpleSuccessfulApp extends App {

  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")

    // ENABLE SERIALIZE CHECK
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-messages = on"))
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-creators = on"))

    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  val actor = system.actorOf(TestActor.props(), TestActor.name)
  actor ! "1"
  actor ! "2"
  actor ! "3"

  System.in.read

}


object TestFailedActor {
  def props() = Props(classOf[TestFailedActor], new TestClass)
  def name = "TestFailedActor"
}

class TestFailedActor(handler: TestClass) extends Actor {
  import context._

  def service: (String => Future[String]) = handler.get

  override def receive: Receive = {
    case url: String => service(url).onComplete(x => println(s"$self : $x"))
  }
}

object SimpleFailedApp extends App {

  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")

    // ENABLE SERIALIZE CHECK
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-messages = on"))
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-creators = on"))

    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  val actor = system.actorOf(TestFailedActor.props(), TestFailedActor.name)
  actor ! "1"
  actor ! "2"
  actor ! "3"

  System.in.read

}


class TestSprayClass()(implicit val context: ActorContext) {

  implicit lazy val ec: ExecutionContext = context.dispatcher

  def pipeline = (
    sendReceive
      ~> unmarshal[String]
    )

  def get(url: String): Future[String] = {
    pipeline {
      Get(url)
    }
  }

}

object TestUnexpectedFailedActor {
  def props() = Props[TestUnexpectedFailedActor]
  def name = "TestUnexpectedFailedActor"
}

class TestUnexpectedFailedActor extends Actor {
  import context._

  def service: (String => Future[String]) = new TestSprayClass()(context).get

  override def receive: Receive = {
    case url: String => service(url).onComplete(x => println(s"$self : $x"))
  }
}


object SimpleUnexpectedFailedApp extends App {

  val cfg = ConfigFactory.parseString(s"akka.cluster.roles=[TEST]")

    // ENABLE SERIALIZE CHECK
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-messages = on"))
    .withFallback(ConfigFactory.parseString("akka.actor.serialize-creators = on"))

    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=127.0.0.1"))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("TestSystem", cfg)
  val actor = system.actorOf(TestUnexpectedFailedActor.props(), TestUnexpectedFailedActor.name)
  actor ! "1"
  actor ! "2"
  actor ! "3"

  System.in.read

}
