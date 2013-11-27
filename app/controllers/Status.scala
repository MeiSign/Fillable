package controllers

import helper.services.RequirementsService
import helper.utils.{AuthenticatedAction}
import play.api.mvc.Controller
import play.api.mvc.Action
import views._
import esclient.ElasticsearchClient


object Status extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def test() = Action { Ok(html.helper.test()) }

  def index() = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val requirementsService = new RequirementsService(ElasticsearchClient.elasticClient)
          requirementsService.runTests map {
            result => Ok(html.status.status(true :: result, result.filter(value => value).length, result.filter(value => !value).length + 1))
          } recover {
            case _ => Ok(html.status.status(List(false,false,false), 3, 0))
          }
        }
      }
    }
  }
}
