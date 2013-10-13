package controllers

import play.api._
import play.api.mvc._

object ApplicationApi extends Controller {

  def status = TODO
  
  def getCompletions(toBeCompleted: String) = Action {
    request => Ok("Got Request [" + request + "] with string [" + toBeCompleted +"]")
  }

  def addSuggestion(suggestion: String) = Action {
    request => Ok("Got Request [" + request + "] with string [" + suggestion +"]")
  }
  
}