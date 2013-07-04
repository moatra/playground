import com.google.javascript.jscomp.CompilerOptions
import sbt._
import Keys._
import play.Project._
import sbtjslint.Plugin._
import sbtjslint.Plugin.LintKeys._
import com.gu.SbtJasminePlugin._

object ApplicationBuild extends Build {

  val appName         = "Play2Bootstrap"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "com.typesafe.play" %% "play-slick" % "0.3.3",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.github.tototoshi" %% "slick-joda-mapper" % "0.3.0",
    "com.github.nscala-time" %% "nscala-time" % "0.4.2"
  )

  val localSettings = lintSettings ++ inConfig(Compile)(Seq(
    // jslint
    sourceDirectory in jslint <<= baseDirectory(_ / "app" / "assets" / "js"),
    excludeFilter in jslint := "lib", // "generated" || "lib"
    flags in jslint := Seq("nomen", "browser"),
    predefs in jslint := Seq("require", "define"),
    LintKeys.explode in (Compile, jslint) := true
  ))

  val defaultOptions = new CompilerOptions()
  defaultOptions.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5)

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    localSettings ++ closureCompilerSettings(defaultOptions) ++ jasmineSettings ++ Seq(
      scalaVersion := "2.10.2",
      requireJs += "main.js",
      requireJsShim += "shim.js",
      requireJsFolder := "js",
      appJsDir <+= baseDirectory(_ / "app" / "assets" / "js"),
      appJsLibDir <+= baseDirectory(_ / "app" / "assets" / "js" / "lib"),
      jasmineTestDir <+= baseDirectory(_ / "test" / "js"),
      jasmineConfFile <+= baseDirectory(_ / "test" / "js" / "test.dependencies.js"),
      jasmineRequireJsFile <+= baseDirectory(_ / "app" / "assets" / "js" / "lib" / "require.js"),
      jasmineRequireConfFile <+= baseDirectory(_ / "app" / "assets" / "js" / "shim.js"),
      (compile in Test) <<= (compile in Test) dependsOn (jslint in Compile),
      (test in Test) <<= (test in Test) dependsOn (jasmine),
      lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" * "style.less")
    ): _*
  )

}
