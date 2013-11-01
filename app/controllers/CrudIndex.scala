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
import esclient.queries.GetFillableIndexQuery
import esclient.queries.EditFillableIndexQuery
import esclient.queries.FillableIndexReregisterQuery

object CrudIndex extends Controller {
  val indexNamePattern = "^[a-zA-Z0-9_]*$".r

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val indexForm: Form[Index] = Form(
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

  def createForm = Authenticated {
    Action.async { implicit request =>
      EsClient.execute(new IndexExistsQuery("fbl_indices", "indices")) flatMap {
        index =>
          {
            if (index.status == 200) {
              Future.successful(Ok(html.crudindex.form(indexForm)))
            } else {
              EsClient.execute(new FillableSetupQuery()) map {
                indexCreated =>
                  if (indexCreated.status == 200) Ok(html.crudindex.form(indexForm, "success", Messages("success.setupComplete")))
                  else Ok(html.crudindex.form(indexForm, "error", Messages("error.indexNotCreated"), true))
              } recover {
                case e: Throwable => Ok(html.crudindex.form(indexForm, "error", Messages("error.indexNotCreated"), true))
              }
            }
          }
      } recover {
        case e: ConnectException => Ok(html.crudindex.form(indexForm, "error", Messages("error.connectionRefused", EsClient.url), true))
        case e: Throwable => Ok(html.crudindex.form(indexForm, "error", Messages("error.couldNotGetIndex"), true))
      }
    }
  }

  def editForm(indexName: String) = {
    Authenticated {
      Action.async {
        implicit request =>
          {
            EsClient.execute(new GetFillableIndexQuery(indexName)) map {
              indices =>
                {
                  val indexSeq: Seq[Index] = (indices.json \\ "_source") map {
                    index =>
                      {
                        index.validate[Index].getOrElse(new Index("", 0, 0))
                      }
                  }
                  if (indexSeq.isEmpty) Ok(html.crudindex.form(indexForm, "error", Messages("error.indexNotFound", indexName), true))
                  else Ok(html.crudindex.form(indexForm.fill(indexSeq.head), "", "", false, true))
                }
            } recover {
              case e: ConnectException => Ok(html.listindices.indexList(Seq(), "error", Messages("error.connectionRefused", EsClient.url)))
              case e: Throwable => Ok(html.listindices.indexList(Seq(), "error", Messages("error.couldNotGetIndex")))
            }
          }
      }
    }
  }

  def submitNewIndex = Authenticated {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          EsClient.execute(new FillableIndexCreateQuery(index.name, index.shards, index.replicas)) map {
            indexCreated =>
              {
                if (indexCreated.status == 200) {
                  EsClient.execute(new FillableIndexRegisterQuery(index.name, index.shards, index.replicas))
                  Ok(html.crudindex.snippetSummary(index.name, "success", Messages("success.indexCreated")))
                } else {
                  (indexCreated.json \ "error") match {
                    case error if error.toString.contains("IndexAlreadyExistsException") =>
                      Ok(html.crudindex.form(
                        indexForm.fill(Index(index.name, index.shards, index.replicas)),
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

  def submitEditIndex = Authenticated {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          EsClient.execute(new EditFillableIndexQuery(index.name, index.replicas)) map {
            indexUpdated =>
              {
                if (indexUpdated.status == 200) {
                  EsClient.execute(new FillableIndexReregisterQuery(index.name, index.shards, index.replicas))
                  Redirect(routes.ListIndices.index)
                } else {
                  (indexUpdated.json \ "error") match {
                    case error =>
                      Ok(html.crudindex.form(
                        indexForm.fill(Index(index.name, index.shards, index.replicas)),
                        "error",
                        Messages("error.unkownUpdateError", "fbl_" + index.name),
                        false))
                  }
                }
              }
          }
          Future.successful(Redirect(routes.ListIndices.index))
        })
    }
  }
}