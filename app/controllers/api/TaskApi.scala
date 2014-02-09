package controllers.api

import play.api._
import play.api.mvc.{Request, WrappedRequest, SimpleResult, ActionBuilder, Controller, Action}
import play.api.libs.json._
import models.{Tasks, TaskTable, Task}
import play.api.Play
import scala.concurrent.Future
import slick.driver.PostgresDriver.simple._
import play.api.db.slick._
import org.joda.time.{DateTime, DateTimeZone}


object TaskApi extends Controller {
  implicit val taskJson = Json.format[Task]

  private def constrain(i: Int, min: Int, max: Int) = {
    Math.min(max, Math.max(min, i))
  }

  def getList(page: Int, size: Int) = DBAction { implicit rs =>
    val p = constrain(page, 0, Int.MaxValue)
    val s = constrain(size, 1, 50)
    Ok(Json.obj(
      "page" -> p,
      "size" -> s,
      "total" -> Tasks.count(),
      "items" -> Tasks.get(p, s)
    ))
  }

  def schema = Action {
    val blank = Task(None, "", completed=false, DateTime.now(DateTimeZone.UTC), 0)
    Ok(Json.toJson(blank))
  }

  def count = DBAction { implicit rs =>
    Ok(Json.obj("count" -> Tasks.count))
  }

  def get(id: Long) = DBAction { implicit rs =>
    Tasks.get(id).map(t => Ok(Json.toJson(t))).getOrElse(NotFound)
  }

  def create = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[Task].map {
      case t: Task => {
        val newTask = Tasks.save(t.copy(id=None))
        val location : String = routes.TaskApi.get(newTask.id.get).url
        Created(Json.toJson(newTask)).withHeaders(
          LOCATION -> location
        )
      }
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(id: Long) = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[Task].map {
      case t: Task => Ok(Json.toJson(Tasks.save(t.copy(id=Some(id)))))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }

  def delete(id: Long) = DBAction { implicit rs =>
    Tasks.delete(id)
    NoContent
  }
}
