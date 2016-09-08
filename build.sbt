import sbt.Keys._

val project = Project(
  id = "demo-akka-spray-serialization",
  base = file("."),
  settings = Seq(
    name := "demo-akka-spray-serialization",
    scalaVersion := "2.11.8",

    libraryDependencies ++= {
      val akkaVersion = "2.4.10"
      val sprayVersion = "1.3.2"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
        "io.spray" %% "spray-client" % sprayVersion
      )
    },

    parallelExecution in Test := false
  )
)
