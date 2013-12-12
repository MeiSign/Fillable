package controllers

import helper.services.RequirementsService
import helper.utils.AuthenticatedAction
import play.api.mvc.Controller
import play.api.mvc.Action
import views._
import esclient.Elasticsearch

object Status extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def test() = Action { Ok(html.helper.test()) }

  def index() = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val requirementsService = new RequirementsService(new Elasticsearch)
          requirementsService.runTests map {
            result => {
              requirementsService.esClient.close()
              Ok(html.status.status(true :: result, result.count(value => value), result.count(value => !value) + 1))
            }
          } recover {
            case _ => {
              requirementsService.esClient.close()
              Ok(html.status.status(List(false,false,false), 3, 0))
            }
          }
        }
      }
    }
  }
}
