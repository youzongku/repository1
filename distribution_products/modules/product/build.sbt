name := """product"""

version := "1.0-SNAPSHOT"

lazy val product = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "hk.tomtop" %% "common" % "1.0",
  "com.tomtop.website" % "data-transfer-object" % "0.0.1-SNAPSHOT" changing(),
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "org.apache.commons" % "commons-collections4" % "4.1",
  "commons-collections" % "commons-collections" % "3.2.1",
  "org.elasticsearch" % "elasticsearch" % "1.4.2",
  "com.markusjura" %% "swagger-play2" % "1.3.7",
  "com.wordnik" %% "swagger-play2" % "1.3.12",
  "io.swagger" % "swagger-core" % "1.5.8",
  "io.swagger" %% "swagger-scala-module" % "1.0.2"
)