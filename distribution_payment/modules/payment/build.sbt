name := "payment"

organization := "com.tomtopcn.distribution"

version := "1.0-SNAPSHOT"

lazy val payment = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "com.google.inject.extensions" % "guice-multibindings" % "3.0",
  "hk.tomtop" %% "common" % "1.0",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-beanutils" % "commons-beanutils" % "1.9.2",
  "org.eclipse.persistence" % "eclipselink" % "2.5.1",
  "org.apache.httpcomponents" % "httpcore" % "4.3",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "org.apache.cxf" % "cxf-rt-frontend-jaxws" % "2.3.3",
  "org.apache.cxf" % "cxf-rt-transports-http" % "2.3.3",
  "org.springframework" % "spring-expression" % "4.0.3.RELEASE",
  "org.springframework" % "spring-aop" % "4.0.3.RELEASE"
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