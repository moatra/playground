import sbt._
import com.typesafe.config._

object BuildConf extends Plugin {
  lazy val conf = ConfigFactory.parseFile(new File("conf/application.conf"))

  def getString(key: String) = conf.getString(key)
}
