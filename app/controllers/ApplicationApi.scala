package controllers

import play.api.mvc._
import esclient.EsClient
import play.api.libs.json._
import scala.concurrent.Future
import play.api.libs.ws.Response
import esclient.EsQuery

object ApplicationApi extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def status = TODO

  //  def getCompletions(indexName: String, toBeCompleted: String) = Action.async {
  //   
  //  }
  //
  //  def addSuggestion(indexName: String, suggestion: String) = Action.async {
  //    
  //  }
  //
  //  def checkRequirements() = Action.async {
  //  
  //  }

  def getCompletions(indexName: String, toBeCompleted: String) = TODO
  def addSuggestion(indexName: String, suggestion: String) = TODO
  def checkRequirements() = TODO
}