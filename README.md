# demo-akka-spray-serialization

### Run Seed Node
[SprayApiServiceSeedNodeApp](https://github.com/lashchenko/demo-akka-spray-serialization/blob/master/src/main/scala/ua/akka/serialization/App.scala#L93) will produce output:
```
API REQUEST: Method GET Endpoint http://google.com with Content:

API RESPONSE:
<HTML><HEAD><meta http-equiv="content-type" content="text/html;charset=utf-8">
<TITLE>302 Moved</TITLE></HEAD><BODY>
<H1>302 Moved</H1>
The document has moved
<A HREF="http://www.google.com.ua/?gfe_rd=cr&amp;ei=HKTRV_uqOY6Bogaf-oKYAw">here</A>.
</BODY></HTML>

Actor[akka://TestSystem/user/ServiceSupervisorActor/SprayApiService/c1#-394021438] : Success(<HTML><HEAD><meta http-equiv="content-type" content="text/html;charset=utf-8">
<TITLE>302 Moved</TITLE></HEAD><BODY>
<H1>302 Moved</H1>
The document has moved
<A HREF="http://www.google.com.ua/?gfe_rd=cr&amp;ei=HKTRV_uqOY6Bogaf-oKYAw">here</A>.
</BODY></HTML>
)
```

### Run another node
[SprayApiServiceNodeApp](https://github.com/lashchenko/demo-akka-spray-serialization/blob/master/src/main/scala/ua/akka/serialization/App.scala#L106) will produce output:
```
API REQUEST: Method GET Endpoint http://google.com with Content:

API RESPONSE:
<HTML><HEAD><meta http-equiv="content-type" content="text/html;charset=utf-8">
<TITLE>302 Moved</TITLE></HEAD><BODY>
<H1>302 Moved</H1>
The document has moved
<A HREF="http://www.google.com.ua/?gfe_rd=cr&amp;ei=uaTRV4C3LaqhogantoWwBw">here</A>.
</BODY></HTML>

Actor[akka://TestSystem/remote/akka.tcp/TestSystem@127.0.0.1:56117/user/ServiceSupervisorActor/SprayApiService/c2#135729572] : Success(<HTML><HEAD><meta http-equiv="content-type" content="text/html;charset=utf-8">
<TITLE>302 Moved</TITLE></HEAD><BODY>
<H1>302 Moved</H1>
The document has moved
<A HREF="http://www.google.com.ua/?gfe_rd=cr&amp;ei=uaTRV4C3LaqhogantoWwBw">here</A>.
</BODY></HTML>
)
```

### Run node with 'serialize-creators = on'
[SprayApiServiceFailedApp](https://github.com/lashchenko/demo-akka-spray-serialization/blob/master/src/main/scala/ua/akka/serialization/App.scala#L119) will throw exception:
```
[ERROR] [09/08/2016 20:52:49.509] [TestSystem-akka.actor.default-dispatcher-14] [akka://TestSystem/user/ServiceSupervisorActor/SprayApiService] pre-creation serialization check failed at [akka://TestSystem/user/IO-HTTP]
java.lang.IllegalArgumentException: pre-creation serialization check failed at [akka://TestSystem/user/IO-HTTP]
	at akka.actor.dungeon.Children$class.makeChild(Children.scala:260)
	at akka.actor.dungeon.Children$class.attachChild(Children.scala:46)
	at akka.actor.ActorCell.attachChild(ActorCell.scala:374)
	at akka.actor.ActorSystemImpl.actorOf(ActorSystem.scala:571)
	at spray.can.HttpExt.<init>(Http.scala:152)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:422)
	at akka.actor.ReflectiveDynamicAccess$$anonfun$createInstanceFor$2.apply(ReflectiveDynamicAccess.scala:32)
	at scala.util.Try$.apply(Try.scala:192)
	at akka.actor.ReflectiveDynamicAccess.createInstanceFor(ReflectiveDynamicAccess.scala:27)
	at akka.actor.ExtensionKey.createExtension(Extension.scala:153)
	at akka.actor.ActorSystemImpl.registerExtension(ActorSystem.scala:737)
	at akka.actor.ExtensionId$class.apply(Extension.scala:79)
	at akka.actor.ExtensionKey.apply(Extension.scala:149)
	at akka.io.IO$.apply(IO.scala:26)
	at spray.client.pipelining$.sendReceive(pipelining.scala:35)
	at ua.akka.serialization.TestSprayApiService.pipeline(App.scala:28)
	at ua.akka.serialization.TestSprayApiService.get(App.scala:43)
	at ua.akka.serialization.SprayApiServiceActor$$anonfun$receive$1.applyOrElse(App.scala:68)
	at akka.actor.Actor$class.aroundReceive(Actor.scala:484)
	at ua.akka.serialization.SprayApiServiceActor.aroundReceive(App.scala:56)
	at akka.actor.ActorCell.receiveMessage(ActorCell.scala:526)
	at akka.actor.ActorCell.invoke(ActorCell.scala:495)
	at akka.dispatch.Mailbox.processMailbox(Mailbox.scala:257)
	at akka.dispatch.Mailbox.run(Mailbox.scala:224)
	at akka.dispatch.Mailbox.exec(Mailbox.scala:234)
	at scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
	at scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
	at scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
	at scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
Caused by: java.io.NotSerializableException: spray.can.HttpExt
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1184)
	at java.io.ObjectOutputStream.defaultWriteFields(ObjectOutputStream.java:1548)
	at java.io.ObjectOutputStream.writeSerialData(ObjectOutputStream.java:1509)
	at java.io.ObjectOutputStream.writeOrdinaryObject(ObjectOutputStream.java:1432)
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1178)
	at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:348)
	at akka.serialization.JavaSerializer$$anonfun$toBinary$1.apply$mcV$sp(Serializer.scala:235)
	at akka.serialization.JavaSerializer$$anonfun$toBinary$1.apply(Serializer.scala:235)
	at akka.serialization.JavaSerializer$$anonfun$toBinary$1.apply(Serializer.scala:235)
	at scala.util.DynamicVariable.withValue(DynamicVariable.scala:58)
	at akka.serialization.JavaSerializer.toBinary(Serializer.scala:235)
	at akka.actor.dungeon.Children$$anonfun$makeChild$2.apply(Children.scala:250)
	at akka.actor.dungeon.Children$$anonfun$makeChild$2.apply(Children.scala:244)
	at scala.collection.LinearSeqOptimized$class.forall(LinearSeqOptimized.scala:83)
	at scala.collection.immutable.List.forall(List.scala:84)
	at akka.actor.dungeon.Children$class.makeChild(Children.scala:244)
	... 31 more
```
