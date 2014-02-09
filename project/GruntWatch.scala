import sbt._
import Keys._
import java.net._
import play.PlayRunHook

/*
https://gist.github.com/leon/6669138
*/
object GruntWatch {
  def apply(): PlayRunHook = {

    object GruntProcess extends PlayRunHook {

      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        process = Some(Process("grunt watch").run)
      }

      override def afterStopped(): Unit = {
        process.map(p => p.destroy())
        process = None
      }
    }

    GruntProcess
  }
}
