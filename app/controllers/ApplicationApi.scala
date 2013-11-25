package controllers

import play.api.mvc._
import play.api.Play
import collection.JavaConversions._
import helper.services.AutoCompletionService
import helper.utils.ElasticsearchClient

object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  val allowAllOrigins = Play.current.configuration.getBoolean("fillable.allowAllOrigins").getOrElse(false)
  val originWhitelist: List[String] = Play.current.configuration.getStringList("fillable.originWhitelist").map(_.toList).getOrElse(List())

  def getOptions(indexName: String, toBeCompleted: String) = Action.async {
    implicit request => {
      val respHeader = if (allowAllOrigins || originWhitelist.contains(request.host)) (ACCESS_CONTROL_ALLOW_ORIGIN -> "*") else ("" -> "")
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(ElasticsearchClient.elasticClient)
      autoCompletionService.getOptions(indexName, toBeCompleted) map {
        json => Ok(json).withHeaders(respHeader)
      }
    }
  }

  def addOption(indexName: String) = Action.async {
    implicit request => {
      val respHeader = if (allowAllOrigins || originWhitelist.contains(request.host)) (ACCESS_CONTROL_ALLOW_ORIGIN -> "*") else ("" -> "")
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(ElasticsearchClient.elasticClient)

      val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
      val typed: Option[String] = map.getOrElse("typed", List()).headOption
      val chosen: Option[String] = map.getOrElse("chosen", List()).headOption
      autoCompletionService.addOption(indexName, typed, chosen) map {
        statusCode => Ok(autoCompletionService.getJsonResponse(statusCode)).withHeaders(respHeader)
      }
    }
  }
}