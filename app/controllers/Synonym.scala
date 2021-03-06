package controllers

import play.api.mvc._
import views.html
import helper.utils.{SynonymSyntaxValidator, AuthenticatedAction}
import play.api.data.Form
import models.{InputTopListEntry, Synonyms}
import play.api.data.Forms._
import scala.Some
import helper.services.SynonymService
import esclient.Elasticsearch
import play.api.i18n.Messages
import play.api.data.validation.{Invalid, Valid, ValidationError, Constraint}
import models.results.EditSynonymsResult

object Synonym extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val synonymSyntaxConstraint: Constraint[String] = Constraint("constraints.synonymSyntaxCheck")({
    plainText =>
      val errors = plainText match {
        case syntaxError if SynonymSyntaxValidator.isWrongSyntax(plainText) => Seq(ValidationError(Messages("error.wrongSyntaxInLine", SynonymSyntaxValidator.getIncorrectSyntaxLineNo(plainText), SynonymSyntaxValidator.getIncorrectSyntaxLine(plainText))))
        case _ => Nil
      }
      if (errors.isEmpty) Valid
      else Invalid(errors)
  })

  val synonymForm: Form[Synonyms] = Form(
    mapping("synonyms" -> text().verifying(synonymSyntaxConstraint)) {
      (synonyms) => Synonyms(synonyms)
    } {
      synonyms => Some(synonyms.text)
    })

  def editor(indexName: String) = {
    AuthenticatedAction {
      Action.async {
        implicit request =>
        {
          val synonymService = new SynonymService(new Elasticsearch)
          synonymService.getSynonymsAndTopInputValues(indexName) map {
            result => {
              if (result.hasError) {
                Redirect(routes.ListIndices.index(Option.empty[String]))
              }
              else {
                Ok(html.synonym.editor(indexName, synonymForm.fill(Synonyms(result.synonymGroups.mkString("\n"))), result.topTenInputValues))
              }
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
          val synonymService = new SynonymService(new Elasticsearch)
          synonymForm.bindFromRequest.fold(
            errors => {
              synonymService.getTopInputValues(indexName) map {
                result => {
                  Ok(html.synonym.editor(indexName, errors, result))
                }
              } recover {
                case _ => {
                  Ok(html.synonym.editor(indexName, errors, List.empty[InputTopListEntry]))
                }
              }

            },
            synonym => {
              synonymService.editSynonyms(indexName, synonym.text) map {
                synonymsAdded: EditSynonymsResult =>
                  if (synonymsAdded.hasError) Redirect(routes.Synonym.editor(indexName)).flashing("error" -> synonymsAdded.error)
                  else Redirect(routes.Synonym.editor(indexName)).flashing("success" -> Messages("success.synonymsAdded", synonymsAdded.reindexResult.succeeded))
              }
            }
          )
        }
      }
    }
  }
}
