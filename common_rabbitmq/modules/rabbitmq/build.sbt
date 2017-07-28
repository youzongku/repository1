name := "rabbitmq"

organization := "com.tomtopcn.common"

version := "1.0-SNAPSHOT"

lazy val rabbitmq = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "com.google.inject.extensions" % "guice-multibindings" % "3.0",
  "com.tomtop.website" %% "common" % "1.0-SNAPSHOT",
  "commons-collections" % "commons-collections" % "3.2.1",
  "com.rabbitmq" % "amqp-client" % "3.6.1",
  "org.quartz-scheduler" % "quartz" % "2.2.2",
  "commons-io" % "commons-io" % "2.2",
  "commons-cli" % "commons-cli" % "1.3.1",
  "com.rabbitmq" % "amqp-client" % "3.6.1"
  
)

sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

publishTo := {
  val repo = "http://192.168.220.54:8080/artifactory/"
  if (isSnapshot.value)
    Some("snapshots" at repo + "libs-snapshot-local")
  else
    Some("releases"  at repo + "libs-release-local")
}