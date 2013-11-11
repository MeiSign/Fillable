package controllers

import play.api.mvc._
import esclient.EsClient
import play.api.libs.json._
import scala.concurrent.Future
import esclient.queries.{AddOptionDocumentQuery, GetDocumentById, GetOptionsQuery}
import models.OptionDocument

object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getOptions(indexName: String, toBeCompleted: String) = Action.async {
    EsClient.execute(new GetOptionsQuery(indexName, toBeCompleted)) map {
      options => {
        Ok((options.json \ indexName).asInstanceOf[JsArray](0)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
      }
    }
  }

  def addOption(indexName: String, typed: Option[String], chosen: Option[String]) = Action.async {
    if (!typed.getOrElse("").equals("")) {
      val docIdString: String = if (chosen.isDefined) chosen.get else typed.get
      EsClient.execute(new GetDocumentById(indexName, docIdString)) flatMap {
        document => {
          val doc: OptionDocument = document.json.validate[OptionDocument].getOrElse(OptionDocument(List[String](), "", 0))
          if (doc.isEmpty) {
            EsClient.execute(new AddOptionDocumentQuery(indexName, docIdString, OptionDocument(List(typed.get), typed.get, 0))) map {
              result => Ok(Json.obj("status" -> "added new option")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
            }
          } else {
            val input: List[String] = if(doc.input.contains(typed.get)) doc.input else (typed.get :: doc.input)
            EsClient.execute(new AddOptionDocumentQuery(indexName, docIdString, OptionDocument(input, doc.output, doc.weight + 1))) map {
              result => Ok(Json.obj("status" -> "extended option")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
            }
          }
        }
      }
    } else {
      Future.successful(Ok(Json.obj("status" -> "nothing to add")))
    }
  }
}