package controllers

import play.api.mvc.Controller
import helper.AuthenticatedAction
import play.api.mvc.Action
import scala.concurrent.Future
import views._
import play.api.Play
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import esclient.{EsClient, EsQuery}
import play.api.libs.json.Json
import play.api.i18n.Messages
import esclient.queries.GetEsVersionQuery

object Status extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index() = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val tests: Future[List[Boolean]] = for {
            pwChanged <- testCredentialsChanged
            version <- testElasticsearchVersion
          } yield {
            List(pwChanged, version)
          }

          tests map {
            result => Ok(html.status.status(result, 4, 1))
          } recover {
            case _ => Ok("")
          }
        }
      }
    }
  }

  def testCredentialsChanged: Future[Boolean] = {
    if (Play.current.configuration.getString("fillable.user").getOrElse("").equals("admin") &&
      Play.current.configuration.getString("fillable.password").getOrElse("").equals("pass123"))
      Future.successful(false)
    else
      Future.successful(true)
  }

  def testElasticsearchVersion: Future[Boolean] = {
    EsClient.execute(new GetEsVersionQuery) map {
      esVersion => {
        val version: Option[String] = (esVersion.json \ "version" \ "number").asOpt[String]
        val buildTimestamp: Option[String] = (esVersion.json \ "version" \ "build_timestamp").asOpt[String]

        if (version.isDefined && buildTimestamp.isDefined) {
          try {
            isAfterOrEqualToRequiredBuildtime(buildTimestamp.get)
          } catch {
            case _ => false
          }
        } else {
          false
        }
      }
    }
  }

  def isAfterOrEqualToRequiredBuildtime(buildtime: String): Boolean = {
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    (fmt.parseDateTime(buildtime).isAfter((fmt.parseDateTime("2013-09-17T12:50:20Z"))) || fmt.parseDateTime(buildtime).isEqual((fmt.parseDateTime("2013-09-17T12:50:20Z"))))
  }
}
