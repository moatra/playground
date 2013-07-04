import sbt._

// This plugin is used to load the sbt-jasmine plugin into our project. This allows us to import the SbtJasminePlugin file
// in Build.scala, and then set the settings and configuration for Sbt-Jasmine
object Plugins extends Build {
  lazy val plugins = Project("plugins", file("."))
    .dependsOn(uri("git://github.com/moatra/sbt-jasmine-plugin.git#0.8.1"))
}