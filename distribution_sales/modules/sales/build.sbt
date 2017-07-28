name := "sales"

organization := "com.tomtopcn.distribution"

version := "1.0-SNAPSHOT"

lazy val sales = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "com.google.inject.extensions" % "guice-multibindings" % "3.0",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-collections" % "commons-collections" % "3.2.1",
  "hk.tomtop" %% "common" % "1.0",
  "com.jd.open.api" % "open-api-sdk" % "2.0",
  "com.markusjura" %% "swagger-play2" % "1.3.7",
   "com.wordnik" %% "swagger-play2" % "1.3.12",
   "io.swagger" % "swagger-core" % "1.5.8",
   "io.swagger" %% "swagger-scala-module" % "1.0.2",
   "com.youzan.open.sdk" % "open-sdk" % "2.0.0"
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