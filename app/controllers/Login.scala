package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.mvc.Results._

object Login extends Controller {

  val loginForm: Form[User] = Form(
    mapping(
      "name" -> text,
      "pw" -> text) {
        (name, pw) => User(name, pw)
      } {
        user => Some(user.name, user.pw)
      })

  def login = Action {
    Ok(html.login.form(loginForm))
  }
  
  def logout = Action {
    Ok("loggedOut").withNewSession
  }

  def submit = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => Ok(html.login.form(errors)),
      user => if (user.name.equals("user") && user.pw.equals("pw"))
        Redirect(request.session.get("returnUrl").getOrElse("/")).withSession(session + ("loggedIn" -> user.name) - "returnUrl")
      else
        Ok(html.login.form(loginForm)))
  }
}