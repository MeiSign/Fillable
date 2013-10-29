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

object CreateIndex extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val createIndexForm: Form[Index] = Form(
    mapping(
      "indexname" -> text(minLength = 4)) {
        (indexname) => Index(indexname)
      } {
        index => Some(index.name)
      })

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
                  else Ok(html.createindex.form(createIndexForm, "error", Messages("error.indexNotCreated")))
              } recover {
                case e: Throwable => Ok(html.createindex.form(createIndexForm, "error", Messages("error.indexNotCreated")))
              }
            }
          }
      } recover {
        case e: ConnectException => Ok(html.createindex.form(createIndexForm, "error", Messages("error.connectionRefused", EsClient.url)))
        case e: Throwable => Ok(html.createindex.form(createIndexForm, "error", Messages("error.couldNotGetIndex")))
      }
    }
  }

  def submit = Authenticated {
    Action.async { implicit request =>
      createIndexForm.bindFromRequest.fold(
        errors => Future.successful(Ok(html.createindex.form(errors))),
        index => {
          for {
            indexCreate <- EsClient.execute(new FillableIndexCreateQuery(index.name))
            indexRegister <- EsClient.execute(new FillableIndexRegisterQuery(index.name))
          } yield {
            Ok(indexCreate.json)
          }
        })
    }
  }
}