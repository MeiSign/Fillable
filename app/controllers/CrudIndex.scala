package controllers

import _root_.helper.services.{AutoCompletionService, CrudIndexService, IndicesStatsService}
import _root_.helper.utils.{SynonymSyntaxValidator, AuthenticatedAction, IndexNameValidator}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._
import scala.concurrent._
import scala.Some
import esclient.Elasticsearch
import play.api.data.validation.{Invalid, Valid, ValidationError, Constraint}
import models.results.BulkImportResult

object CrudIndex extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val indexForm: Form[Index] = Form(
    mapping(
      "indexname" -> nonEmptyText(minLength = 4).verifying(Messages("error.noSpecialchars"), indexname => IndexNameValidator.containsOnlyValidChars(indexname)),
      "shards" -> number(min = 0, max = 10),
      "replicas" -> number(min = 0, max = 10),
      "logging" -> boolean) {
        (indexname, shards, replicas, logging) => Index(indexname, shards, replicas, logging)
      } {
        index => Some(index.name, index.shards, index.replicas, index.logging)
      })

  def createForm = AuthenticatedAction {
    Action {
      implicit request => Ok(html.crudindex.form(indexForm.fill(Index("", 4, 0, true))))
    }
  }

  def editForm(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val indicesStatsService = new IndicesStatsService(new Elasticsearch)
          indicesStatsService.getIndexSettings(indexName) map {
            case index if index.isEmpty => {
              Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
            }
            case index => {
              Ok(html.crudindex.form(indexForm.fill(index.getOrElse(Index("", 0, 0))), true))
            }
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
          val crudIndexService = new CrudIndexService(new Elasticsearch)
          crudIndexService.createFillableIndex(index.name, index.shards, index.replicas, index.logging) map {
            case 200 => {
              Redirect(routes.CrudIndex.showSummary("fbl_" + index.name)).flashing("success" -> Messages("success.indexCreated"))
            }
            case 400 => {
              Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.unableToCreateIndex"))
            }
            case _ => {
              Redirect(routes.CrudIndex.createForm).flashing("error" -> Messages("error.unableToCreateIndex"))
            }
          }
        }
      )
    }
  }

  def showSummary(indexName: String) = AuthenticatedAction {
    Action {
      implicit request => {
        Ok(html.crudindex.snippetSummary("http://" + request.host, indexName.toLowerCase))
      }
    }
  }

  def submitEditIndex = AuthenticatedAction {
    Action.async { implicit request =>
      indexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.crudindex.form(errors))),
        index => {
          val crudIndexService: CrudIndexService = new CrudIndexService(new Elasticsearch)
          crudIndexService.editFillableIndex(index.name, index.replicas) map {
            case 200 => {
              Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
            }
            case _ => {
              Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("error" -> Messages("error.unkownUpdateError", "fbl_" + index.name))
            }
          }
        }
      )
    }
  }

  def deleteIndex(indexName: String) = AuthenticatedAction {
    Action.async {
      implicit request => {
        val crudIndexService: CrudIndexService = new CrudIndexService(new Elasticsearch)
        crudIndexService.deleteFillableIndex(indexName) map {
          case 200 => {
            Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
          }
          case 404 => {
            Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
          }
          case _ => {
            Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.unkownUpdateError", "fbl_" + indexName))
          }
        }
      }
    }
  }

  val completionsForm: Form[Completions] = Form(
    mapping("completions" -> nonEmptyText(maxLength = 20000)) {
      (completions) => Completions(completions)
    } {
      completions => Some(completions.text)
    })

  def importCompletionsForm(indexName: String) = AuthenticatedAction {
    Action {
      implicit request => {
        Ok(html.crudindex.importCompletionsForm(indexName, completionsForm.fill(Completions(""))))
      }
    }
  }

  def importCompletionsSubmit(indexName: String)  = AuthenticatedAction {
    Action.async(parse.maxLength(46080 ,parse.urlFormEncoded)) {
      implicit request => {
        request.body match {
          case Left(MaxSizeExceeded(length)) => Future.successful(Redirect(routes.CrudIndex.importCompletionsForm(indexName)).flashing("error" -> Messages("error.maxlength")))
          case Right(body) =>
            completionsForm.bindFromRequest(body).fold(
              errors => Future.successful(Ok(html.crudindex.importCompletionsForm(indexName, errors))),
              completions => {
                val autoCompletionService = new AutoCompletionService(new Elasticsearch)
                autoCompletionService.importCompletions(indexName, completions.text) map {
                  result: BulkImportResult =>
                    if (result.error) Redirect(routes.ListIndices.index(Option.empty[String])).flashing("error" -> Messages("error.bulkItemsFailed", result.failures))
                    else Redirect(routes.ListIndices.index(Option.empty[String])).flashing("success" -> Messages("success.completionsAdded", result.requests))
                }
              }
            )
        }
      }
    }
  }

  def uploadCompletionsFile(indexName: String) = AuthenticatedAction {
    Action.async(parse.multipartFormData) {
      request => {
        request.body.file("completionsFile").map {
          completions => {
            val autoCompletionService = new AutoCompletionService(new Elasticsearch)
            autoCompletionService.importCompletionsFromFile(indexName, completions.ref) map {
              result: BulkImportResult =>
              if (result.error) Redirect(routes.ListIndices.index(Option.empty[String])).flashing("error" -> Messages("error.bulkItemsFailed", result.failures))
              else Redirect(routes.ListIndices.index(Option.empty[String])).flashing("success" -> Messages("success.completionsAdded", result.requests))
            }
          }
        }.getOrElse(Future.successful(Redirect(routes.ListIndices.index(Option.empty[String])).flashing("success" -> Messages("success.completionsAdded"))))
      }
    }
  }
}