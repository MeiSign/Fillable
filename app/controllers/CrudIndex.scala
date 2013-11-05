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
                  if (indexCreated.status == 200) Redirect(routes.CrudIndex.createForm).flashing("success" -> Messages("success.setupComplete"))
                  else Ok(html.crudindex.form(indexForm, true)).flashing("error" -> Messages("error.indexNotCreated"))
              } recover {
                case e: Throwable => Ok(html.crudindex.form(indexForm, true)).flashing("error" -> Messages("error.indexNotCreated"))
              }
            }
          }
      } recover {
        case e: ConnectException => Redirect(routes.Status.index()).flashing("error" -> Messages("error.connectionRefused", EsClient.url))
        case e: Throwable => Redirect(routes.Status.index()).flashing("error" -> Messages("error.couldNotGetIndex"))
      }
    }
  }

  def editForm(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
          {
            EsClient.execute(new GetFillableIndexQuery(indexName)) map {
              index => {
                if (index.status == 200) {
                  val indexJson = index.json.validate[Index].getOrElse(new Index("", 0, 0))
                  Ok(html.crudindex.form(indexForm.fill(indexJson), false, true))
                } else {
                  Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
                }
              }
            } recover {
              case e: ConnectException => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.connectionRefused", EsClient.url))
              case e: Throwable => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
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
          EsClient.execute(new CreateFillableIndexQuery(index.name, index.shards, index.replicas)) flatMap {
            indexCreated =>
              {
                if (indexCreated.status == 200) {
                  EsClient.execute(new FillableIndexRegisterQuery(index.name, index.shards, index.replicas)) map {
                    indexRegistered => {
                      if (indexRegistered.status == 201) {
                        Redirect(routes.CrudIndex.showSummary(index.name)).flashing("success" -> Messages("success.indexCreated"))
                      } else {
                        Redirect(routes.CrudIndex.showSummary(index.name)).flashing("error" -> Messages("error.indexCreatedButNotRegistered"))
                      }
                    }
                  } recover {
                    case _ => Redirect(routes.CrudIndex.showSummary(index.name)).flashing("error" -> Messages("error.indexCreatedButNotRegistered"))
                  }
                } else {
                  (indexCreated.json \ "error") match {
                    case error if error.toString.contains("IndexAlreadyExistsException") =>
                      Future.successful(
                        Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.indexAlreadyExists", "fbl_" + index.name)))
                    case _ => Future.successful(Redirect(routes.CrudIndex.createForm).flashing("error" -> Messages("error.unableToCreateIndex")))
                  }
                }
              }
          } recover {
            case e: ConnectException => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.connectionRefused", EsClient.url))
            case e: Throwable => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.unableToCreateIndex"))
          }
        })
    }
  }

  def showSummary(indexName: String) = AuthenticatedAction {
    Action {
      implicit request => Ok(html.crudindex.snippetSummary(indexName))
    }
  }

  def submitEditIndex = AuthenticatedAction {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          EsClient.execute(new EditFillableIndexQuery(index.name, index.replicas)) flatMap {
            indexUpdated => {
              if (indexUpdated.status == 200) {
                EsClient.execute(new FillableIndexReregisterQuery(index.name, index.shards, index.replicas)) map {
                  indexChangeRegistered => {
                    if (indexChangeRegistered.status == 200) {
                      Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
                    } else {
                      Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("error" -> Messages("error.indexUpdatedButNotRegistered"))
                    }
                  }
                } recover {
                  case _ => Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("error" -> Messages("error.indexUpdatedButNotRegistered"))
                }
              } else {
                (indexUpdated.json \ "error") match {
                  case error =>
                    Future.successful(
                      Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("error" -> Messages("error.unkownUpdateError", "fbl_" + index.name))
                    )
                }
              }
            }
          } recover {
            case e: ConnectException => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.connectionRefused", EsClient.url))
            case e: Throwable => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.unableToUpdateIndex"))
          }
        }
      )
    }
  }
  
  def deleteIndex(indexName: String) = AuthenticatedAction {
    Action.async { implicit request =>
      EsClient.execute(new DeleteFillableIndexQuery(indexName)) flatMap {
        indexDeleted => {
          if (indexDeleted.status == 200) {
            EsClient.execute(new FillableIndexUnregisterQuery(indexName)) map {
              indexUnregistered => if (indexUnregistered.status == 200) {
                Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
              } else {
                Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("error" -> Messages("error.deleteIndexFailed"))
              }
            } recover {
              case _ => Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("error" -> Messages("error.deleteIndexFailed"))
            }
          } else {
            (indexDeleted.json \ "error") match {
              case error if error.toString.contains("IndexMissingException") =>
                Future.successful(Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName)))
            }
          }
        }
      } recover {
        case e: ConnectException => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.connectionRefused", EsClient.url))
        case e: Throwable => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.deleteIndexFailed"))
      }
    }
  }
}