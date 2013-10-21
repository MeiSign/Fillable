package esclient

import play.api.Play
import play.api.libs.json.JsResult
import scala.concurrent.Future
import play.api.libs.ws._
import play.Logger
import play.api.libs.json.Reads
import play.api.libs.json.JsValue

object EsClient {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  
  val url: String = { 
    Play.current.configuration.getString("esclient.url") getOrElse {
      Logger.warn("Couldn't find any elasticsearch host. Trying to use Localhost")   
    "127.0.0.1:9200"}
	}

	def execute(query: EsQuery): Future[JsValue] = {
	  query.httpType match {
	    case HttpType.Get => WS.url(url + query.getUrlAddon).get().map { response => response.json }
	    case HttpType.Post => WS.url(url + query.getUrlAddon).post(query.toJson).map { response => response.json }  
	  }
	  
	}
	
	def logException(e: Exception) = {
	  e.getMessage() match {
	    case msg: String if msg.startsWith("Connection refused:") => Logger.error("Connection refused from " + url + ". Elasticsearch up and running? Original Exception: " + msg )
	    case msg: String => Logger.error("Unknown Error: " + msg + e)
	  } 
	}
}