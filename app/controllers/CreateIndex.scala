package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
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

object CreateIndex extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val createIndexForm: Form[Index] = Form(
    mapping(
      "indexname" -> text(minLength = 4)) {
        (indexname) => Index(indexname)
      } {
        index => Some(index.name)
      })

  def form = Action {
    Ok(html.createindex.form(createIndexForm))
  }

  def submit = Action.async { implicit request =>
    createIndexForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(html.createindex.form(errors))),
      index => {
        val result: Future[Response] = EsClient.execute(new IndexExistsQuery("fbl_indices"))
        result flatMap {
          case result if result.status == 404 => {
            val createIndexResult: Future[Response] = EsClient.execute(new FillableSetupQuery()) 
            createIndexResult map {
              indexCreated => Ok(indexCreated.json)
            } recover {
              case e: Throwable => BadRequest(e.getMessage())
            }
          } recover {
            case e: Throwable => BadRequest(e.getMessage())
          }
          case result if result.status == 200 => Future.successful(Response) 
        } flatMap {
          _ => {
            for {
              indexCreate <- EsClient.execute(new FillableIndexCreateQuery(index.name))
              indexRegister <- EsClient.execute(new FillableIndexRegisterQuery(index.name))
            } yield {
              Ok(indexCreate.json)
            }
          }
        } recover {
          case e: Throwable => BadRequest(e.getMessage())
        }
      })
  }
}