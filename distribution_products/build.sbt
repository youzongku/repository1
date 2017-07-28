name := """distribution_products"""

version := "1.0-SNAPSHOT"

lazy val product = (project in file("modules/product"))
				.enablePlugins(PlayJava)
	
lazy val root = (project in file("."))
				.enablePlugins(PlayJava)
				.dependsOn(product)
				.aggregate(product)
				.settings(aggregate in update := false)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
	filters,
  	"postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)


LessKeys.verbose in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

javacOptions ++= Seq("-encoding", "UTF-8")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator
