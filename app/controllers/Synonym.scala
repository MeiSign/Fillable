package controllers

import play.api.mvc._
import views.html
import helper.utils.AuthenticatedAction
import scala.concurrent.Future
import play.api.data.Form
import models.Synonyms
import play.api.data.Forms._
import scala.Some
import helper.services.{CrudIndexService, SynonymService}
import esclient.ElasticsearchClient
import play.api.i18n.Messages

object Synonym extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val synonymForm: Form[Synonyms] = Form(
    mapping(
      "synonyms" -> nonEmptyText(minLength = 4)) {
      (synonyms) => Synonyms(synonyms)
    } {
      synonyms => Some(synonyms.text)
    })

  def editor(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val synonymService = new SynonymService(ElasticsearchClient.elasticClient)
          synonymService.getSynonymsAndTopInputValues(indexName) map {
            result => {
              if (result.hasError) Redirect(routes.ListIndices.index(Option.empty[String]))
              else Ok(html.synonym.editor(indexName, synonymForm.fill(Synonyms(result.synonymGroups.mkString("\n"))), result.topTenInputValues))
            }
          } recover {
            case e: Throwable => {
              Redirect(routes.ListIndices.index(Option.empty[String]))
            }
          }
        }
      }
    }
  }

  def submitSynonyms(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          synonymForm.bindFromRequest.fold(
            errors => Future.successful(Ok(html.synonym.editor(indexName, errors, List.empty[String]))),
            synonym => {
              val synonymService = new SynonymService(ElasticsearchClient.elasticClient)
              synonymService.editSynonyms(indexName, synonym.text) map {
                editSynonymsResponse => editSynonymsResponse match {
                  case 200 => Redirect(routes.Synonym.editor(indexName)).flashing("success" -> Messages("success.synonymsAdded"))
                  case _ => Redirect(routes.Synonym.editor(indexName)).flashing("error" -> Messages("error.unknownErrorSynonymEdit"))
                }
              }
            }
          )
        }
      }
    }
  }
}
