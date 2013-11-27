package controllers

import play.api.mvc._
import helper.utils.AuthenticatedAction
import esclient.ElasticsearchClient
import views.html
import helper.services.{LogIndexService, LogStatsService}
import play.api.i18n.Messages

object Logs extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logStatsService = new LogStatsService(ElasticsearchClient.elasticClient)
          logStatsService.getLogLists map {
            lists => Ok(html.logs.logList(lists))
          }
        }
      }
    }
  }

  def activateLogging(name: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logIndexService = new LogIndexService(ElasticsearchClient.elasticClient)
          logIndexService.createLogIndex(name, 4, 0) map {
            lists => Redirect(routes.Logs.index)
          }
        }
      }
    }
  }

  def deactivateLogging(name: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logIndexService = new LogIndexService(ElasticsearchClient.elasticClient)
          logIndexService.deleteLogIndex(name) map {
            lists => Redirect(routes.Logs.index)
          }
        }
      }
    }
  }
}
