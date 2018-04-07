resolvers += "JBoss" at "https://repository.jboss.org/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

//Scala.js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.20")
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.3")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1")

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.9.2" % Compile,
  "com.typesafe.play" %% "play-json" % "2.6.7" % Compile,
  "io.monix" %% "monix" % "2.3.0",
  "com.koddi" %% "geocoder" % "1.0.2" % Compile
)

