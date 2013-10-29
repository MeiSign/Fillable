package controllers

import play.api.mvc.Action
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.mvc.SimpleResult
import play.api.mvc.Results.Redirect

case class Authenticated[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    if (request.session.get("user").getOrElse("").equals("user")) { action(request) 
    } else {
      Future.successful(Redirect("/login").withSession(("returnUrl", request.path)))
    }
  }

  lazy val parser = action.parser
}