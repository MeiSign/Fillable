package esclient

import play.api.Play
import play.api.libs.json.JsResult

object EsClient {
	val url: Option[String] = Play.current.configuration.getString("esclient.url")
	
	def execute(): EsResult = {
	  
	  new EsResult
	}
}