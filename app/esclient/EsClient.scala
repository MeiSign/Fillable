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

object EsClient {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val url: String = {
    Play.current.configuration.getString("esclient.url") getOrElse {
      Logger.warn(Messages("error.hostConfigMissing"))
      "127.0.0.1:9200"
    }
  }

  def execute(query: EsQuery): Future[Response] = {
    println("execute")
    query.httpType match {
      case HttpType.Get => WS.url(url + query.getUrlAddon).get()
      case HttpType.Post => WS.url(url + query.getUrlAddon).post(query.toJson)
      case HttpType.Put => WS.url(url + query.getUrlAddon).put(query.toJson)
      case HttpType.Head => WS.url(url + query.getUrlAddon).head()
      case _ => WS.url(url + query.getUrlAddon).get()
    }
  }

  def logException(e: Exception) = {
    e.getMessage() match {
      case msg: String if msg.startsWith("Connection refused:") => Logger.error(Messages("error.connectionRefused"), url, msg)
      case msg: String => Logger.error("Unknown Error: " + e)
    }
  }
}