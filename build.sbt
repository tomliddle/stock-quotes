name := "stock-quotes"

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  evolutions,
  "com.h2database" % "h2" % "1.4.191",
  cache,
  ws,
  specs2 % Test)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
