import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.filters.gzip.GzipFilter
import play.api.Play.current
import scala.concurrent.Future

object Global extends WithFilters(new GzipFilter(shouldGzip = (request, response) => {
  Play.isProd && response.headers.get("Content-Type").exists(_.startsWith("text/html"))
})) with GlobalSettings {
  override def onError(request: RequestHeader, ex: Throwable)  = {
    Future.successful(InternalServerError(
      views.html.error_page()
    ))
  }
  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound(
      views.html.not_found()
    ))
  }
  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(BadRequest("Bad Request: " + error))
  }
}
