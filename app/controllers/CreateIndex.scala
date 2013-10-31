package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._
import play.api.libs.ws.WS
import play.api.libs.ws.Response
import scala.concurrent._
import esclient.EsClient
import esclient.queries.IndexExistsQuery
import esclient.queries.FillableSetupQuery
import esclient.queries.FillableIndexCreateQuery
import esclient.queries.FillableIndexRegisterQuery
import java.net.ConnectException
import scala.util.matching.Regex

object CreateIndex extends Controller {
  val indexNamePattern = "^[a-zA-Z0-9]*$".r

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val createIndexForm: Form[Index] = Form(
    mapping(
      "indexname" -> nonEmptyText(minLength = 4).verifying(Messages("error.noSpecialchars"), indexname => containsOnlyValidChars(indexname, indexNamePattern)),
      "shards" -> number(min = 0, max = 10),
      "replicas" -> number(min = 0, max = 10)) {
        (indexname, shards, replicas) => Index(indexname, shards, replicas)
      } {
        (index => Some(index.name, index.shards, index.replicas))
      })

  def containsOnlyValidChars(name: String, pattern: Regex): Boolean = {
    pattern.findAllIn(name).mkString.length == name.length
  }

  def form = Authenticated {
    Action.async { implicit request =>
      EsClient.execute(new IndexExistsQuery("fbl_indices", "indices")) flatMap {
        index =>
          {
            if (index.status == 200) {
              Future.successful(Ok(html.createindex.form(createIndexForm)))
            } else {
              EsClient.execute(new FillableSetupQuery()) map {
                indexCreated =>
                  if (indexCreated.status == 200) Ok(html.createindex.form(createIndexForm, "success", Messages("success.setupComplete")))
                  else Ok(html.createindex.form(createIndexForm, "error", Messages("error.indexNotCreated"), true))
              } recover {
                case e: Throwable => Ok(html.createindex.form(createIndexForm, "error", Messages("error.indexNotCreated"), true))
              }
            }
          }
      } recover {
        case e: ConnectException => Ok(html.createindex.form(createIndexForm, "error", Messages("error.connectionRefused", EsClient.url), true))
        case e: Throwable => Ok(html.createindex.form(createIndexForm, "error", Messages("error.couldNotGetIndex"), true))
      }
    }
  }

  def submit = Authenticated {
    Action.async { implicit request =>
      createIndexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.createindex.form(errors))),
        index => {
          EsClient.execute(new FillableIndexCreateQuery(index.name, index.shards, index.replicas)) map {
            indexCreated =>
              {
                if (indexCreated.status == 200) {
                  EsClient.execute(new FillableIndexRegisterQuery(index.name, index.shards, index.replicas))
                  Ok(html.createindex.snippetSummary(index.name, "success", Messages("success.indexCreated")))
                } else {
                  (indexCreated.json \ "error") match {
                    case error if error.toString.contains("IndexAlreadyExistsException") =>
                      Ok(html.createindex.form(
                        createIndexForm.fill(Index(index.name, index.shards, index.replicas)),
                        "error",
                        Messages("error.indexAlreadyExists", "fbl_" + index.name),
                        false))
                  }
                }
              }
          }
        })
    }
  }
}