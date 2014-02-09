import scala.slick.driver.PostgresDriver.simple._

package object models {
  def Tasks(implicit session: Session) = new TaskDao
}
