package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def docs = TODO

  def section(section: String) = TODO

  def tour = TODO

  def about = TODO

  def register = TODO

  def login = TODO
}
