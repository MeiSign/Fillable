package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object CreateIndex extends Controller {
  
  val createIndexForm: Form[Index] = Form(   
    mapping(
      "indexname" -> text(minLength = 4)
    )
    {
      (indexname) => Index(indexname) 
    } 
    {
      index => Some(index.name)
    }
  )
  
  def form = Action {
    Ok(html.createindex.form(createIndexForm))
  }
  
  def submit = Action { implicit request =>
    createIndexForm.bindFromRequest.fold(
      errors => BadRequest(html.createindex.form(errors)),
      index => Ok(html.createindex.form(createIndexForm.fill(index)))
    )
  }
}