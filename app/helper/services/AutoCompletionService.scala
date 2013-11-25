package helper.services

import play.api.libs.json.{Json, JsValue, JsArray}
import esclient.EsClient
import esclient.queries.{AddOptionDocumentQuery, GetOptionsQuery, GetDocumentByIdQuery}
import scala.concurrent._
import org.elasticsearch.client.Client
import scala.language.implicitConversions
import models.OptionDocument
import org.elasticsearch.indices.IndexMissingException

class AutoCompletionService(esClient: => Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getJsonResponse(statusCode: Int): JsValue = {
    statusCode match {
      case 404 => Json.obj("status" -> "index is missing")
      case 400 => Json.obj("status" -> "error")
      case 200 => Json.obj("status" -> "added new option")
      case 202 => Json.obj("status" -> "extended option")
      case 204 => Json.obj("status" -> "nothing to add")
      case _ => Json.obj("status" -> "unknown")
    }
  }

  def addOption(indexNameParam: String, typed: Option[String], chosen: Option[String]): Future[Int] = {
    indexNameParam match {
      case emptyName if (emptyName.isEmpty) => Future.successful(400)
      case indexName => {
        typed.getOrElse("") match {
          case emptyTypedString if (emptyTypedString.isEmpty) => Future.successful(204)
          case typedString => {
            val documentId = getDocumentId(typedString, chosen)
            addOptionToEs(indexName, documentId, typedString)
          }
        }
      }
    }
  }

  def addOptionToEs(indexName: String, docIdString: String, typed: String): Future[Int] = {
    val queryFuture = GetDocumentByIdQuery(esClient, indexName, indexName, docIdString.hashCode.toString).execute

    queryFuture flatMap {
      document => {
        if (!document.isExists) {
          val doc = OptionDocument(typed, typed, 0).toJsonBuilder
          val addQuery = AddOptionDocumentQuery(esClient, indexName, docIdString, doc.string())
          addQuery.execute map {
            result => 200
          }
        } else {
          val doc = OptionDocument().fromBuilder(document)
          val addQuery = AddOptionDocumentQuery(esClient, indexName, docIdString, doc.extendOption().toJsonBuilder.string())
          addQuery.execute map {
            result => 202
          }
        }
      }
    } recover {
      case document: IndexMissingException => 404
    }
  }

  def getOptions(indexName: String, toBeCompleted: String): Future[JsArray] = {
    if (indexName.isEmpty || toBeCompleted.isEmpty) {
      Future.successful(Json.arr())
    } else {
      EsClient.execute(new GetOptionsQuery(indexName, toBeCompleted)) map {
        options => {
          ((options.json \ indexName).asInstanceOf[JsArray](0) \ "options").asInstanceOf[JsArray]
        }
      } recover {
        case _ => Json.arr()
      }
    }
  }

  def getDocumentId(typed: String, chosen: Option[String]): String = {
    chosen.getOrElse("") match {
      case emptyChosenString if (emptyChosenString.isEmpty) => typed
      case nonEmptyChosenString => nonEmptyChosenString
    }
  }
}
