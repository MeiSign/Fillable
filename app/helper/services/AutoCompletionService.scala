package helper.services

import play.api.libs.json.{Json, JsValue}
import scala.concurrent._
import scala.language.implicitConversions
import models.OptionDocument
import org.elasticsearch.indices.IndexMissingException
import org.elasticsearch.search.suggest.completion.CompletionSuggestion
import scala.collection.JavaConversions._
import esclient.queries.{AddOptionDocumentsBulkQuery, GetDocumentByIdQuery, AddOptionDocumentQuery, GetOptionsQuery}
import esclient.Elasticsearch
import models.results.BulkImportResult
import scala.io.Source
import play.api.libs.Files.TemporaryFile

class AutoCompletionService(es: Elasticsearch) {
  val esClient = es.client
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
    val logIndexService = new LogIndexService(es)
    indexNameParam match {
      case emptyName if emptyName.isEmpty => Future.successful(400)
      case indexName => {
        typed.getOrElse("") match {
          case emptyTypedString if emptyTypedString.isEmpty => Future.successful(204)
          case typedString => {
            val documentId = getDocumentId(typedString, chosen)
            val result = for {
              optionAdded <- addOptionToEs(indexName, documentId, typedString.toLowerCase)
              optionLogged <- logIndexService.addLogEntry(indexName + "_log", typedString, chosen.getOrElse("").toLowerCase)
            } yield optionAdded
            result
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
            result => {
              200
            }
          }
        } else {
          val doc = OptionDocument().fromBuilder(document)
          val addQuery = AddOptionDocumentQuery(esClient, indexName, docIdString, doc.extendOption().toJsonBuilder.string())
          addQuery.execute map {
            result => {
              202
            }
          }
        }
      }
    } recover {
      case document: IndexMissingException => 404
    }
  }

  def getOptions(indexName: String, toBeCompleted: String): Future[JsValue] = {
    if (indexName.isEmpty || toBeCompleted.isEmpty) {
      Future.successful(Json.arr())
    } else {
      GetOptionsQuery(esClient, indexName, toBeCompleted.toLowerCase).execute map {
        options => {
          val completion: CompletionSuggestion = options.getSuggest.getSuggestion(indexName)
          val entries = completion.getEntries.get(0).getOptions.iterator().toList

          val scalaOptions: List[String] = entries map {
            entry => entry.getText.string()
          }

          Json.toJson(scalaOptions)
        }
      } recover {
        case _ => Json.arr()
      }
    }
  }

  def getDocumentId(typed: String, chosen: Option[String]): String = {
    chosen.getOrElse("") match {
      case emptyChosenString if emptyChosenString.isEmpty => typed.toLowerCase
      case nonEmptyChosenString => nonEmptyChosenString.toLowerCase
    }
  }

  def importCompletions(indexName: String, completions: String): Future[BulkImportResult] = {
    bulkImportOptions(indexName, getCompletionsListFromString(completions))
  }

  def getCompletionsListFromString(completions: String) = completions.split(",") map(entry => entry.trim)
  def getCompletionsListFromFile(completions: TemporaryFile) = {
    val source = Source.fromFile(completions.file)
    val result = source.mkString.split(",") map(entry => entry.trim)
    source.close()
    result
  }

  def importCompletionsFromFile(indexName: String, completions: TemporaryFile) = {
    bulkImportOptions(indexName, getCompletionsListFromFile(completions))
  }

  def bulkImportOptions(indexName: String, list: Array[String]): Future[BulkImportResult] = {
    val docs: Array[(String, OptionDocument)] = list map (docEntry => (docEntry.toLowerCase, OptionDocument(docEntry, docEntry, 0)))
    val bulkQuery = AddOptionDocumentsBulkQuery(esClient, indexName, docs.toMap[String, OptionDocument])
    bulkQuery.execute map {
      result => BulkImportResult(result.hasFailures, result.getItems.count(request => request.isFailed), result.getItems.length)
    }
  }
}
