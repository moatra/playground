package controllers

import play.api._
import play.api.mvc._
import play.api.templates.HtmlFormat
import play.api.Play.current

object Frontend extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def data = Action {
    Ok(views.html.data())
  }

  def documentation = Action {
    Ok(views.html.documentation())
  }

  def about = Action {
    Ok(views.html.about())
  }

  def register = TODO

  def login = TODO
}
