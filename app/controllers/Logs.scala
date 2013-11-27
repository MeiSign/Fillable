package controllers

import play.api.mvc._
import helper.utils.AuthenticatedAction
import esclient.ElasticsearchClient
import views.html
import models.LogListEntry
import scala.concurrent.Future

object Logs extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          Future.successful(Ok(html.logs.logList(List.empty[LogListEntry])))
        }
      }
    }
  }
}
