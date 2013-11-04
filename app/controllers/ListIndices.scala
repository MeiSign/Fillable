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

  def index(highlightIndex: Option[String], level: Option[String], msg: Option[String]) = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
          {
            println("Indexresult: " + highlightIndex.getOrElse(""))
            EsClient.execute(new GetFillableIndicesQuery) map {
              indices => { 
                Ok(html.listindices.indexList((indices.json \\ "_source") map {
                index => { 
                  index.validate[Index].getOrElse(new Index("", 0, 0)) }
              }, highlightIndex.getOrElse(""), level.getOrElse(""), msg.getOrElse(""))) }
            } recover {
              case e: ConnectException => Ok(html.listindices.indexList(Seq(), "error", Messages("error.connectionRefused", EsClient.url)))
              case e: Throwable => Ok(html.listindices.indexList(Seq(), "error", Messages("error.couldNotGetIndex")))
            }
          }
      }
    }
  }
}