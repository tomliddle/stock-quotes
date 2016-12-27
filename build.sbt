lazy val scalaV = "2.11.8"

lazy val backend = (project in file("backend")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(frontend),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
    //evolutions,
    "com.h2database" % "h2" % "1.4.191",
    cache,
    ws,
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    specs2 % Test
  ),
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).enablePlugins(PlayScala)
    .dependsOn(sharedJvm)

lazy val frontend = (project in file("frontend")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.lihaoyi" %%% "upickle" % "0.4.3"
  ),
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val common = (crossProject.crossType(CrossType.Pure) in file("common")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSWeb)

// TODO check if I need this to compile the fastOptJS code for scalajas
lazy val sharedJvm = common.jvm.settings(
  (resources in Compile) += (fastOptJS in (sharedJs, Compile)).value.data
)
lazy val sharedJs = common.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project backend", _: State)) compose (onLoad in Global).value