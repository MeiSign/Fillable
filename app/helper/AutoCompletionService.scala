package helper

import play.api.libs.json.{Json, JsValue, JsArray}
import models.OptionDocument
import esclient.EsClient
import esclient.queries.{GetOptionsQuery, AddOptionDocumentQuery, GetDocumentByIdQuery}
import scala.concurrent.Future

class AutoCompletionService {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

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
    EsClient.execute(new GetDocumentByIdQuery(indexName, docIdString)) flatMap {
      document => {
        val doc: OptionDocument = parseDocument(document.json)
        if (doc.isEmpty) {
          EsClient.execute(new AddOptionDocumentQuery(indexName, docIdString, OptionDocument(List(typed), typed, 0))) map {
            result => 200
          }
        } else {
          val input: List[String] = if(doc.input.contains(typed)) doc.input else (typed :: doc.input)
          EsClient.execute(new AddOptionDocumentQuery(indexName, docIdString, OptionDocument(input, doc.output, doc.weight + 1))) map {
            result => 202
          }
        }
      }
    }
  }

  def getOptions(indexName: String, toBeCompleted: String): Future[JsArray] = {
    if (indexName.isEmpty || toBeCompleted.isEmpty) {
      Future.successful(Json.arr())
    } else {
      EsClient.execute(new GetOptionsQuery(indexName, toBeCompleted)) map {
        options => (options.json \ indexName).asInstanceOf[JsArray](0).asInstanceOf[JsArray]
      } recover {
        case _ => Json.arr()
      }
    }
  }

  def parseDocument(json: JsValue): OptionDocument = {
    json.validate[OptionDocument].getOrElse(OptionDocument(List[String](), "", 0))
  }

  def getDocumentId(typed: String, chosen: Option[String]): String = {
    chosen.getOrElse("") match {
      case emptyChosenString if (emptyChosenString.isEmpty) => typed.hashCode.toString
      case nonEmptyChosenString => nonEmptyChosenString.hashCode.toString
    }
  }
}
