package controllers

import helper.AuthenticatedAction
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._
import scala.concurrent._
import esclient.EsClient
import esclient.queries._
import java.net.ConnectException
import scala.Some

object CrudIndex extends Controller {
  val validIndexChars = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ List('_')).toSet

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val indexForm: Form[Index] = Form(
    mapping(
      "indexname" -> nonEmptyText(minLength = 4).verifying(Messages("error.noSpecialchars"), indexname => containsOnlyValidChars(indexname, validIndexChars)),
      "shards" -> number(min = 0, max = 10),
      "replicas" -> number(min = 0, max = 10)) {
        (indexname, shards, replicas) => Index(indexname, shards, replicas)
      } {
        (index => Some(index.name, index.shards, index.replicas))
      })

  def containsOnlyValidChars(name: String, pattern: Set[Char]): Boolean = name.forall(validIndexChars.contains(_))

  def createForm = AuthenticatedAction {
    Action.async { implicit request =>
      EsClient.execute(new IndexExistsQuery("fbl_indices", "indices")) flatMap {
        index =>
          {
            if (index.status == 200) {
              Future.successful(Ok(html.crudindex.form(indexForm)))
            } else {
              EsClient.execute(new FillableIndexSetupQuery()) map {
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
    AuthenticatedAction {
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

  def submitNewIndex = AuthenticatedAction {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          EsClient.execute(new CreateFillableIndexQuery(index.name, index.shards, index.replicas)) map {
            indexCreated =>
              {
                if (indexCreated.status == 200) {
                  EsClient.execute(new FillableIndexRegisterQuery(index.name, index.shards, index.replicas))
                  // hier noch ein map rein zur sicherheit
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

  def submitEditIndex = AuthenticatedAction {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          EsClient.execute(new EditFillableIndexQuery(index.name, index.replicas)) map {
            indexUpdated =>
              {
                if (indexUpdated.status == 200) {
                  EsClient.execute(new FillableIndexReregisterQuery(index.name, index.shards, index.replicas))
                  // hier noch ein map rein zur sicherheit
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
  
  def deleteIndex(indexName: String) = AuthenticatedAction {
    Action.async { implicit request =>
      EsClient.execute(new DeleteFillableIndexQuery(indexName)) map {
            indexDeleted =>
              {
                if (indexDeleted.status == 200) {
                  EsClient.execute(new FillableIndexUnregisterQuery(indexName))
                  // hier noch ein map rein zur sicherheit
                  Ok("index deleted")
                } else {
                  (indexDeleted.json \ "error") match {
                    case error if error.toString.contains("IndexMissingException") =>
                      Ok("index gibts nicht")
                  }
                }
              }
          }
    }
  }
}