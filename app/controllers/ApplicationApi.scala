package controllers

import play.api._
import play.api.mvc._
import esclient.EsClient

object ApplicationApi extends Controller {

  def status = TODO
  
  def getCompletions(toBeCompleted: String) = Action {
    request => Ok("Got Request [" + request + "] with string [" + toBeCompleted +"] EsClient Url [" +  EsClient.url.getOrElse("tes") + "]")
  }

  def addSuggestion(suggestion: String) = Action {
    request => Ok("Got Request [" + request + "] with string [" + suggestion +"]")
  }
  
}