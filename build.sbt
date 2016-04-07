name := "spray-book-catalog"

version := "0.0.1-SNAPSHOT"

organization := "org.packtpublishing"

scalaVersion := "2.11.7"

parallelExecution in Test := false

libraryDependencies ++= {
  val logbackVersion = "1.1.3"
  val akkaVersion = "2.3.9"
  val sprayVersion = "1.3.3"
  
  Seq(
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "ch.qos.logback" % "logback-core" % logbackVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % "1.3.1",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
    "com.h2database" % "h2" % "1.4.190",
    "com.gettyimages" %% "spray-swagger" % "0.5.1",
    "com.jsuereth" %% "scala-arm" % "1.4",
    "io.spray" %% "spray-testkit" % sprayVersion  % "test",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  )
}

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18)
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true
