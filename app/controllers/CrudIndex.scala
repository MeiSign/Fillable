package controllers

import _root_.helper.services.{CrudIndexService, IndicesStatsService}
import _root_.helper.utils.{ElasticsearchClient, AuthenticatedAction, IndexNameValidator}
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
import play.api.libs.json._
import scala.Some

object CrudIndex extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val indexForm: Form[Index] = Form(
    mapping(
      "indexname" -> nonEmptyText(minLength = 4).verifying(Messages("error.noSpecialchars"), indexname => IndexNameValidator.containsOnlyValidChars(indexname)),
      "shards" -> number(min = 0, max = 10),
      "replicas" -> number(min = 0, max = 10)) {
        (indexname, shards, replicas) => Index(indexname, shards, replicas)
      } {
        index => Some(index.name, index.shards, index.replicas)
      })

  def createForm = AuthenticatedAction { Action { implicit request => Ok(html.crudindex.form(indexForm, false)) }}

  def editForm(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request => {
          val indicesStatsService = new IndicesStatsService(ElasticsearchClient.elasticClient)
          indicesStatsService.getIndexSettings(indexName) map {
            case index if index.isEmpty => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
            case index => Ok(html.crudindex.form(indexForm.fill(index.getOrElse(Index("", 0, 0))), false, true))
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
          val query = new CreateFillableIndexQuery(index.name, index.shards, index.replicas)
          EsClient.execute(query) map {
            indexCreated => {
                if (indexCreated.status == 200) {
                  Redirect(routes.CrudIndex.showSummary(index.name)).flashing("success" -> Messages("success.indexCreated"))
                } else {
                  (indexCreated.json \ "error") match {
                    case error if error.toString.contains("IndexAlreadyExistsException") => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.indexAlreadyExists", "fbl_" + index.name))
                    case _ => Redirect(routes.CrudIndex.createForm).flashing("error" -> Messages("error.unableToCreateIndex"))
                  }
                }
              }
          } recover {
            case e: ConnectException => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.connectionRefused", EsClient.url(query)))
            case e: Throwable => Redirect(routes.CrudIndex.createForm()).flashing("error" -> Messages("error.unableToCreateIndex"))
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
    Action.async { implicit request => {
      val deleteIndexQuery = new DeleteFillableIndexQuery(indexName)
      EsClient.execute(deleteIndexQuery) map {
        indexDeleted => {
          if (indexDeleted.status == 200) {
            Redirect(routes.ListIndices.index(Option[String](indexName))).flashing("success" -> Messages("success.indexWillBeChangedSoon"))
          } else {
            (indexDeleted.json \ "error") match {
              case error if error.toString.contains("IndexMissingException") => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.indexNotFound", indexName))
            }
          }
        }
      } recover {
        case e: ConnectException => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.connectionRefused", EsClient.url(deleteIndexQuery)))
        case e: Throwable => Redirect(routes.ListIndices.index(Option[String](""))).flashing("error" -> Messages("error.deleteIndexFailed"))
      }
    }
  }
  }
}