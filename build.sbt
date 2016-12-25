import sbt.Project.projectToRef
//import play.PlayImport.PlayKeys._

import sbt.Keys._
import sbt.Project.projectToRef

//name := "stock-quotes"

val scalaV = "2.11.7"

//lazy val clients = Seq(frontend)

lazy val root = project.in(file("."))
  .aggregate(frontend, backend, common)

lazy val common = project.settings(
  scalaVersion := scalaV,
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

lazy val backend = (project in file("backend")).settings(
  scalaVersion := scalaV,
  //routesImport += "config.Routes._",
  //scalaJSProjects := clients,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
    evolutions,
    "com.h2database" % "h2" % "1.4.191",
    cache,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
    "org.mockito" % "mockito-core" % "2.3.7" % "test"
  ),
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).enablePlugins(PlayScala).dependsOn(common)

lazy val frontend = (project in file("frontend")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "upickle" % "0.3.4",
    "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
  ),
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).dependsOn(common)
