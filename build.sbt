import com.typesafe.sbt.packager.universal.Keys._
import sbt.Keys.name
import sbt.Keys.version

name := BuildConf.getString("application.name")

version := BuildConf.getString("application.version")

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  jdbc,
  filters,
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.5",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0"
)

play.Project.playScalaSettings

(test in Test) <<= (test in Test) dependsOn gruntTask("test")

dist <<= dist dependsOn gruntTask("compile")

stage <<= stage dependsOn gruntTask("compile")

lessEntryPoints := Nil

javascriptEntryPoints := Nil

playRunHooks <+= baseDirectory.map(_ => GruntWatch())
