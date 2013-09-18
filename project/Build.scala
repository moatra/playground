import com.google.javascript.jscomp.CompilerOptions
import sbt._
import Keys._
import play.Project._
import sbtjslint.Plugin._
import sbtjslint.Plugin.LintKeys._
import com.gu.SbtJasminePlugin._
import com.typesafe.config._
import scala.collection.JavaConversions

object ApplicationBuild extends Build {

  val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
  val appName         = conf.getString("application.name")
  val appVersion      = conf.getString("application.version")

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "com.typesafe.play" %% "play-slick" % "0.4.0",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.github.tototoshi" %% "slick-joda-mapper" % "0.3.0",
    "com.github.nscala-time" %% "nscala-time" % "0.4.2",
    "com.lambdaworks" % "scrypt" % "1.2.0"

  )

  val localSettings = lintSettings ++ inConfig(Compile)(Seq(
    // jslint
    sourceDirectory in jslint <<= baseDirectory(_ / "app" / "assets" / "js"),
    excludeFilter in jslint := "vendor", // "generated" || "lib"
    flags in jslint := Seq("nomen", "browser"),
    predefs in jslint := Seq("require", "define"),
    LintKeys.explode in (Compile, jslint) := true
  ))

  val defaultOptions = new CompilerOptions()
  defaultOptions.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5)
  defaultOptions.setExtraAnnotationNames(JavaConversions.setAsJavaSet(Set("restrict", "ngdoc", "element", "scope",
    "usage", "priority", "paramDescription", "eventOf", "eventType", "methodOf", "propertyOf", "TODO")))

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    localSettings ++ closureCompilerSettings(defaultOptions) ++ jasmineSettings ++ Seq(
      scalaVersion := "2.10.2",
      requireJs += "main.js",
      requireJsShim += "shim.js",
      templatesImport += "models._",
      appJsDir <+= baseDirectory(_ / "app" / "assets" / "javascripts"),
      appJsLibDir <+= baseDirectory(_ / "app" / "assets" / "javascripts" / "vendor"),
      jasmineTestDir <+= baseDirectory(_ / "test" / "js"),
      jasmineConfFile <+= baseDirectory(_ / "test" / "js" / "test.dependencies.js"),
      (compile in Test) <<= (compile in Test) dependsOn (jslint in Compile),
      (test in Test) <<= (test in Test) dependsOn (jasmine),
      lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" * "style.less")
    ): _*
  )

}
