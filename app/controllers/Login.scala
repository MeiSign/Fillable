package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._

object Login extends Controller {

  val loginForm: Form[User] = Form(
    mapping(
      "name" -> nonEmptyText,
      "pw" -> text) {
        (name, pw) => User(name, pw)
      } {
        user => Some(user.name, user.pw)
      })

  def login = Action { implicit request =>
    Ok(html.login.form(loginForm))
  }

  def logout = Action { implicit request =>
    Redirect("/login").withNewSession
  }

  def submit = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => Ok(html.login.form(errors)),
      requestUser => {
        val user: String = Play.current.configuration.getString("fillable.user").getOrElse("")
        val password: String = Play.current.configuration.getString("fillable.password").getOrElse("")
        if (requestUser.name.equals(user) && requestUser.pw.equals(password))
          Redirect(request.session.get("returnUrl").getOrElse("/")).withSession(session + ("user" -> requestUser.name.hashCode.toString) - "returnUrl")
        else
          Redirect(routes.Login.login).flashing("error" -> Messages("error.wrongCredentials"))
      })
  }
}