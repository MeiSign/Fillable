package controllers

import _root_.helper.services.IndicesStatsService
import _root_.helper.utils.AuthenticatedAction
import play.api.mvc._
import esclient.ElasticsearchClient
import views._

object ListIndices extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index(highlightIndex: Option[String]) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val indicesStatsService = new IndicesStatsService(ElasticsearchClient.elasticClient)
          indicesStatsService.getIndexList map {
            list => Ok(html.listindices.indexList(list))
          }
        }
      }
    }
  }
}