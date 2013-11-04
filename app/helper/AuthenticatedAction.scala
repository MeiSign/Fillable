package helper

import play.api.mvc.Action
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.mvc.SimpleResult
import play.api.mvc.Results.Redirect
import play.api.Play

case class AuthenticatedAction[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    if (request.session.get("user").getOrElse("").equals(Play.current.configuration.getString("fillable.user").getOrElse(""))) { 
      action(request) 
    } else {
      Future.successful(Redirect("/login").withSession(("returnUrl", request.path)))
    }
  }

  lazy val parser = action.parser
}