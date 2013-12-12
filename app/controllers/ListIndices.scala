package controllers

import helper.services.IndicesStatsService
import helper.utils.AuthenticatedAction
import play.api.mvc._
import esclient.Elasticsearch
import views._

object ListIndices extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index(highlightIndex: Option[String]) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val indicesStatsService = new IndicesStatsService(new Elasticsearch)
          indicesStatsService.getIndexList map {
            list => {
              indicesStatsService.esClient.close()
              Ok(html.listindices.indexList(list))
            }
          }
        }
      }
    }
  }
}