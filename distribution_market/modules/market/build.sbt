name := "market"

organization := "com.tomtopcn.distribution"

version := "1.0-SNAPSHOT"

lazy val market = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "hk.tomtop" %% "common" % "1.0",
  "javax.activation" % "activation" % "1.1.1",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-collections" % "commons-collections" % "3.2.1",
  "commons-beanutils" % "commons-beanutils" % "1.9.2"
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