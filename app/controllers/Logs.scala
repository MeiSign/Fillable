package controllers

import play.api.mvc._
import helper.utils.AuthenticatedAction
import esclient.Elasticsearch
import views.html
import helper.services.{LogIndexService, LogStatsService}

object Logs extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logStatsService = new LogStatsService(new Elasticsearch)
          logStatsService.getLogLists map {
            lists => {
              logStatsService.esClient.close()
              Ok(html.logs.logList(lists))
            }
          }
        }
      }
    }
  }

  def activateLogging(name: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logIndexService = new LogIndexService(new Elasticsearch)
          logIndexService.createLogIndex(name, 4, 0) map {
            lists => {
              logIndexService.esClient.close()
              Redirect(routes.Logs.index)
            }
          }
        }
      }
    }
  }

  def deactivateLogging(name: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val logIndexService = new LogIndexService(new Elasticsearch)
          logIndexService.deleteLogIndex(name) map {
            lists => {
              logIndexService.esClient.close()
              Redirect(routes.Logs.index)
            }
          }
        }
      }
    }
  }
}
