package controllers

import play.api.mvc._
import esclient.EsClient
import play.api.libs.json._
import scala.concurrent.Future
import esclient.queries.{AddOptionDocumentQuery, GetDocumentByIdQuery, GetOptionsQuery}
import models.OptionDocument
import play.api.Play
import collection.JavaConversions._
import helper.AutoCompletionService


object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  val allowAllOrigins = Play.current.configuration.getBoolean("fillable.allowAllOrigins").getOrElse(false)
  val originWhitelist: List[String] = Play.current.configuration.getStringList("fillable.originWhitelist").map(_.toList).getOrElse(List())

  def getOptions(indexName: String, toBeCompleted: String) = Action.async {
    implicit request => {
      val respHeader = if (allowAllOrigins || originWhitelist.contains(request.host)) (ACCESS_CONTROL_ALLOW_ORIGIN -> "*") else ("" -> "")
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getOptions(indexName, toBeCompleted) map {
        json => Ok(json).withHeaders(respHeader)
      }
    }
  }

  def addOption(indexName: String) = Action.async {
    implicit request => {
      val respHeader = if (allowAllOrigins || originWhitelist.contains(request.host)) (ACCESS_CONTROL_ALLOW_ORIGIN -> "*") else ("" -> "")
      val autoCompletionService: AutoCompletionService = new AutoCompletionService

      val map : Map[String,Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
      val typed: Option[String] = map.getOrElse("typed", List()).headOption
      val chosen: Option[String] = map.getOrElse("chosen", List()).headOption
      autoCompletionService.addOption(indexName, typed, chosen) map {
        statusCode => statusCode match {
          case 400 => Ok(Json.obj("status" -> "error")).withHeaders(respHeader)
          case 200 => Ok(Json.obj("status" -> "added new option")).withHeaders(respHeader)
          case 202 => Ok(Json.obj("status" -> "extended option")).withHeaders(respHeader)
          case 204 => Ok(Json.obj("status" -> "nothing to add")).withHeaders(respHeader)
          case _ => Ok(Json.obj("status" -> "unknown")).withHeaders(respHeader)
        }
      }
    }
  }
}