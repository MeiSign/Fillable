package esclient

import play.api.Play
import play.api.libs.json.JsResult
import scala.concurrent.Future
import play.api.libs.ws._
import play.Logger
import play.api.libs.json.Reads
import play.api.libs.json.JsValue
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.libs.concurrent.Promise
import play.api.libs.ws.Response

object EsClient {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val url: String = {
    Play.current.configuration.getString("esclient.url") getOrElse {
      Logger.error(Messages("error.hostConfigMissing"))
      ""
    }
  }

  def execute(query: EsQuery): Future[JsValue] = {
    query.httpType match {
      case HttpType.Get => WS.url(url + query.getUrlAddon).get().map { response => response.json } 
      case HttpType.Post => WS.url(url + query.getUrlAddon).post(query.toJson).map { response => response.json }
      case HttpType.Put => WS.url(url + query.getUrlAddon).put(query.toJson).map { response => response.json }
      case HttpType.Head => WS.url(url + query.getUrlAddon).head().map { response => response.status.asInstanceOf[JsValue] }
      case _ => WS.url(url + query.getUrlAddon).get().map { response => response.json }
    }
  }
}