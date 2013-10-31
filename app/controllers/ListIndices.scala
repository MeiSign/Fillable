package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import esclient.EsClient
import esclient.queries.GetFillableIndicesQuery
import java.net.ConnectException
import models._
import views._
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.libs.json.JsValue
import org.hamcrest.core.IsInstanceOf
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess

object ListIndices extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  
  def index = {
    Authenticated {
      Action.async {
        implicit request =>
          {
            EsClient.execute(new GetFillableIndicesQuery) map {
              indices => { 
                Ok(html.listindices.indexList((indices.json \\ "_source") map {
                index => { 
                  index.validate[Index].getOrElse(new Index("", 0, 0)) }
              })) }
            } recover {
              case e: ConnectException => Ok(html.listindices.indexList(Seq(), "error", Messages("error.connectionRefused", EsClient.url)))
              case e: Throwable => Ok(html.listindices.indexList(Seq(), "error", Messages("error.couldNotGetIndex")))
            }
          }
      }
    }
  }
}