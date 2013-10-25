package controllers

import play.api.mvc._
import esclient.EsClient
import esclient.queries.GetSuggestionsQuery
import play.api.libs.json._
import scala.concurrent.Future
import esclient.queries.AddSuggestionQuery
import esclient.queries.IndexExistsQuery
import esclient.queries.CreateSuggestionIndexQuery
import play.api.libs.ws.Response
import esclient.queries.GetEsVersionQuery
import esclient.EsQuery

object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  val ErrorJson: JsObject = Json.obj("status" -> "error");

  def status = TODO
  
  def getCompletions(indexName: String, toBeCompleted: String) = Action.async {
    val query: GetSuggestionsQuery = new GetSuggestionsQuery(indexName, toBeCompleted)
    EsClient.execute(query) map { response: Response =>
      Ok(query.getJsResult(response))
    } recover {
      case _: Exception => Ok("recover")
    }
  }

  def addSuggestion(indexName: String, suggestion: String) = Action.async {
    val indexExistsQuery: IndexExistsQuery = new IndexExistsQuery(indexName);
    val addSuggestionQuery: AddSuggestionQuery = new AddSuggestionQuery(indexName, suggestion)

    EsClient.execute(indexExistsQuery) map { response =>
      if (indexExistsQuery.getBooleanResult(response)) Future.successful(Response)
      else EsClient.execute(new CreateSuggestionIndexQuery(indexName))
    } flatMap { _ =>
      EsClient.execute(addSuggestionQuery)
    } map { response: Response =>
      Ok(addSuggestionQuery.getJsResult(response))
    } recover {
      case e: Exception => { EsClient.logException(e); Ok(EsQuery.respondWithError(e.getMessage())) }
      case _ => Ok(EsQuery.respondWithError(""))
    }
  }

  def checkRequirements() = Action.async {
    val query: GetEsVersionQuery = new GetEsVersionQuery()
    EsClient.execute(query).map(response => Ok(query.getJsResult(response))).recover {
      case e: Exception => { EsClient.logException(e); Ok(ErrorJson) }
    }
  }
}