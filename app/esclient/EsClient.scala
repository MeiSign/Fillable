package esclient

import play.api.Play

object EsClient {
	val url: Option[String] = Play.current.configuration.getString("esclient.url")
}