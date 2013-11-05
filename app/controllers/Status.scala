package controllers

import play.api.mvc.Controller
import helper.AuthenticatedAction
import play.api.mvc.Action
import scala.concurrent.Future
import views._

object Status extends Controller {

  def index() = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          Future.successful(Ok(html.status.status()))
        }
      }
    }
  }

}
