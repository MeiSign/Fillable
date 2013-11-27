package controllers

import helper.services.{CrudIndexService, IndicesStatsService}
import helper.utils.{AuthenticatedAction, IndexNameValidator}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import views._
import scala.concurrent._
import scala.Some
import esclient.ElasticsearchClient

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
          val indicesStatsService = new IndicesStatsService(ElasticsearchClient.elasticClient)
          indicesStatsService.getIndexSettings(indexName) map {
            case index if index.isEmpty => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
            case index => Ok(html.crudindex.form(indexForm.fill(index.getOrElse(Index("", 0, 0))), true))
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
          val crudIndexService = new CrudIndexService(ElasticsearchClient.elasticClient)
          crudIndexService.createFillableIndex(index.name, index.shards, index.replicas, index.logging) map {
            indexCreated => indexCreated match {
              case 200 => Redirect(routes.CrudIndex.showSummary(index.name)).flashing("success" -> Messages("success.indexCreated"))
              case 400 => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.unableToCreateIndex"))
              case _ => Redirect(routes.CrudIndex.createForm).flashing("error" -> Messages("error.unableToCreateIndex"))
            }
          }
        })
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
          val crudIndexService: CrudIndexService = new CrudIndexService(ElasticsearchClient.elasticClient)
          crudIndexService.editFillableIndex(index.name, index.replicas) map {
            editStatus => editStatus match {
              case 200 => Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
              case _ => Redirect(routes.ListIndices.index(Option[String](index.name))).flashing("error" -> Messages("error.unkownUpdateError", "fbl_" + index.name))
            }
          }
        }
      )
    }
  }
  
  def deleteIndex(indexName: String) = AuthenticatedAction {
    Action.async {
      implicit request => {
        val crudIndexService: CrudIndexService = new CrudIndexService(ElasticsearchClient.elasticClient)
        crudIndexService.deleteFillableIndex(indexName) map {
          deleteStatus => deleteStatus match {
            case 200 => Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
            case 404 => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
            case _ => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.unkownUpdateError", "fbl_" + indexName))
          }
        }
      }
    }
  }
}