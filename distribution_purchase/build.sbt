name := "distribution_purchase"

organization := "com.tomtopcn.distribution"

version := "1.0-SNAPSHOT"

lazy val purchase = (project in file("modules/purchase")).enablePlugins(PlayJava)

lazy val root = (project in file(".")).enablePlugins(PlayJava)
  .dependsOn(purchase)
  .aggregate(purchase)
  .settings(
    aggregate in update := false
  )

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  filters,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)

javacOptions ++= Seq("-encoding", "UTF-8")

sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

publishTo := {
  val repo = "http://192.168.220.54:8080/artifactory/"
  if (isSnapshot.value)
    Some("snapshots" at repo + "libs-snapshot-local")
  else
    Some("releases"  at repo + "libs-release-local")
}