package controllers

import helper.AuthenticatedAction
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
        implicit request =>
          {
            val getIndicesQuery = new GetFillableIndicesQuery
            EsClient.execute(getIndicesQuery) map {
              indices => { 
                Ok(html.listindices.indexList((indices.json \\ "_source") map {
                index => { 
                  index.validate[Index].getOrElse(new Index("", 0, 0)) }
              }, highlightIndex.getOrElse(""))) }
            } recover {
              case e: ConnectException => Redirect(routes.Status.index()).flashing("error" -> Messages("error.connectionRefused", EsClient.url(getIndicesQuery)))
              case e: Throwable => Redirect(routes.Status.index()).flashing("error" -> Messages("error.couldNotGetIndex"))
            }
          }
      }
    }
  }
}