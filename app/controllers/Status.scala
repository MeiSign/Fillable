package controllers

import play.api.mvc.Controller
import helper.AuthenticatedAction
import play.api.mvc.Action
import scala.concurrent.Future
import views._
import play.api.Play

object Status extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index() = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val tests: Future[List[Boolean]] = for {
            pwChanged <- testCredentialsChanged
          } yield {
            List(pwChanged)
          }

          tests map {
            result => Ok(html.status.status(result, 75, 25))
          } recover {
            case _ => Ok("")
          }
        }
      }
    }
  }

  def testCredentialsChanged: Future[Boolean] = {
    if (Play.current.configuration.getString("fillable.user").getOrElse("").equals("admin") &&
      Play.current.configuration.getString("fillable.password").getOrElse("").equals("pass123"))
      Future.successful(false)
    else
      Future.successful(true)
  }
}
