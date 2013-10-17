package controllers

import play.api._
import play.api.mvc._
import esclient.EsClient
import esclient.queries.FindCompletionsQuery
import play.api.libs.json._
import esclient.queries.CreateCompletionsFieldQuery
import esclient.queries.CreateCompletionsFieldQuery

object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  val ErrorJson: JsObject = Json.obj("status" -> "error");
  
  def status = TODO
  
  def getCompletions(indexName: String, fieldName: String, toBeCompleted: String) = Action.async {
    val query: FindCompletionsQuery = new FindCompletionsQuery(indexName, fieldName, toBeCompleted)
    EsClient.execute(query).map(response => Ok(query.getResult(response))).recover {
      case e: Exception => { EsClient.logException(e); Ok(ErrorJson)}
    }
  }

  def addCompletion(indexName: String, fieldName: String, completion: String) = Action.async {
    val query: CreateCompletionsFieldQuery = new CreateCompletionsFieldQuery(indexName, fieldName)
    EsClient.execute(query).map(response => Ok(query.getResult(response))).recover {
      case e: Exception => { EsClient.logException(e); Ok(ErrorJson)}
    }
    
    
    //request => Ok("Got Request [" + request + "] with string [" + suggestion +"]")
  }
  
}