

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

scalaVersion in ThisBuild := "2.12.2"

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.2"
)

val playJsonVersion = "2.6.7"

lazy val server = project.enablePlugins(PlayScala).settings(
  commonSettings,
  name := "play-scala-server",
  libraryDependencies ++= Seq(
    ws,
    guice,
    "com.vmunier" %% "scalajs-scripts" % "1.1.0",
    "org.webjars" %% "webjars-play" % "2.6.0-M1",
    "org.webjars.bower" % "bulma" % "0.6.1",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
    "org.jsoup" % "jsoup" % "1.10.2" % Test
  ),
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value
).dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  name := "shared",
  commonSettings,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.beachape" %%% "enumeratum" % "1.5.12",
    "com.typesafe.play" %% "play-json" % playJsonVersion
  )
).jsSettings(
  libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.3",
  jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
)
  .jsConfigure(_ enablePlugins ScalaJSWeb)
  .enablePlugins(SbtTwirl)
  .settings(
    sourceDirectories in(Compile, TwirlKeys.compileTemplates) +=
      (baseDirectory.value.getParentFile / "src" / "main" / "scala")
  )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js


lazy val client = project.enablePlugins(ScalaJSPlugin, ScalaJSWeb).configs(IntegrationTest).settings(
  commonSettings ++ Defaults.itSettings,
  mainClass in Compile := Some("App"),
  emitSourceMaps in fullOptJS := true,
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.4",
    "io.surfkit" %%% "scalajs-google-maps" % "0.0.3-SNAPSHOT",
    "com.typesafe.play" %%% "play-json" % playJsonVersion,
    "org.scala-js" %%% "scalajs-java-time" % "0.2.3",
    "org.scalatest" %%% "scalatest" % "3.0.3" % Test
  )
).dependsOn(sharedJs)