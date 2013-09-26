package controllers.api

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.Foo

object FooJson {
  implicit val fooFormat = Json.format[Foo]
}

object FooApi extends Controller {
  import FooJson._

  def get = Action {
    Ok(Json.toJson(Foo("Hello, world!")))
  }
}
