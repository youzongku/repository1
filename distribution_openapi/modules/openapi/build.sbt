name := "openapi"

organization := "com.tomtopcn.distribution"

version := "1.0-SNAPSHOT"

lazy val openapi = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "com.google.inject.extensions" % "guice-multibindings" % "3.0",
  "com.tomtop.website" %% "common" % "1.0-SNAPSHOT",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-collections" % "commons-collections" % "3.2.1"
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