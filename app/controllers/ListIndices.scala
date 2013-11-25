package controllers

import _root_.helper.services.IndicesStatsService
import _root_.helper.utils.AuthenticatedAction
import play.api.mvc._
import esclient.EsClient
import esclient.queries.GetFillableIndicesQuery
import java.net.ConnectException
import models._
import views._
import play.api.i18n.Messages

object ListIndices extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index(highlightIndex: Option[String]) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val indicesStatsService = new IndicesStatsService
          indicesStatsService.getIndexList map {
            list => Ok(html.listindices.indexList(list))
          }
        }
      }
    }
  }
}