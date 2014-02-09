package models

import scala.slick.driver.PostgresDriver.simple._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time._

case class Task(id: Option[Long], description: String, completed: Boolean, created: DateTime, priority: Int)

class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
  // columns
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def description = column[String]("description", O.NotNull)
  def completed = column[Boolean]("completed", O.NotNull)
  def created = column[DateTime]("created", O.NotNull)
  def priority = column[Int]("priority", O.NotNull)

  def * = (id.?, description, completed, created, priority) <> (Task.tupled, Task.unapply)
}

class TaskDao()(implicit val session: Session) {
  private val tasks = TableQuery[TaskTable]

  def save(task: Task) : Task = {
    task.id match {
      case None => {
        val withCreated : Task = task.copy(created=DateTime.now(DateTimeZone.UTC))
        val id = (tasks returning tasks.map(_.id)) += withCreated
        withCreated.copy(id=Some(id))
      }
      case Some(id) => {
        tasks.filter(_.id === id).map(t => (t.description, t.completed, t.priority)).update((task.description, task.completed, task.priority))
        get(id).get
      }
    }
  }

  def delete(task: Task) = {
    tasks.filter(_.id === task.id).delete
  }

  def delete(id: Long) = {
    tasks.filter(_.id === id).delete
  }

  def get(id: Long) : Option[Task] = {
    tasks.filter(_.id === id).firstOption
  }

  def get(page: Int, size: Int) = {
    tasks.sortBy(_.id).drop(page * size).take(size).list
  }

  def count() = {
    Query(tasks.length).first
  }
}
