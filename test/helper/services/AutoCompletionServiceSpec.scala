package helper.services

import org.specs2.mutable._
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import esclient.Elasticsearch
import play.api.test.WithApplication
import play.api.test.FakeApplication

class AutoCompletionServiceSpec extends Specification with Mockito  {

  "AutoCompletionService" should {

    val fakeApp = new FakeApplication(additionalConfiguration = Map("esclient.embeddedElasticsearch" -> false))

    "getDocumentId should return chosen String for nonempty typed and nonempty chosen string" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getDocumentId("typed", Option("chosen")) must beEqualTo("chosen")
    }

    "getDocumentId should return typed String for empty chosen string" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getDocumentId("typed", Option("")) must beEqualTo("typed")
    }

    "getDocumentId should return chosen String for empty typed string" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getDocumentId("", Option("chosen")) must beEqualTo("chosen")
    }

    "addOption should return 400 Bad Request statuscode for empty index parameter" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.addOption("", Option("typed"), Option("Chosen")) must beEqualTo(400).await
    }

    "addOption should return 204 Success statuscode for Option.empty type parameter" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.addOption("index", Option.empty, Option("Chosen")) must beEqualTo(204).await
    }

    "addOption should return 204 Success statuscode for \"\" as type parameter" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.addOption("index", Option(""), Option("Chosen")) must beEqualTo(204).await
    }

    "getOption should return empty js array for empty index parameter" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getOptions("", "toBeCompleted") must beEqualTo(Json.arr()).await
    }

    "getOption should return empty js array for empty toBeCompleted parameter" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getOptions("index", "") must beEqualTo(Json.arr()).await
    }

    "getJsonResponse should return a created json object for 200 statuscode" in new WithApplication(fakeApp) {
      val autoComletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoComletionService.getJsonResponse(200) must beEqualTo(Json.parse("""{"status":"added new option"}"""))
    }

    "getJsonResponse should return an extended json object for 202 statuscode" in new WithApplication(fakeApp) {
      val autoComletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoComletionService.getJsonResponse(202) must beEqualTo(Json.parse("""{"status":"extended option"}"""))
    }

    "getJsonResponse should return a nothing to add json object for 204 statuscode" in new WithApplication(fakeApp) {
      val autoComletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoComletionService.getJsonResponse(204) must beEqualTo(Json.parse("""{"status":"nothing to add"}"""))
    }

    "getJsonResponse should return a bad request json object for 400 statuscode" in new WithApplication(fakeApp) {
      val autoComletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoComletionService.getJsonResponse(400) must beEqualTo(Json.parse("""{"status":"error"}"""))
    }

    "getJsonResponse should return an unkown json object for 706 statuscode" in new WithApplication(fakeApp) {
      val autoComletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoComletionService.getJsonResponse(706) must beEqualTo(Json.parse("""{"status":"unknown"}"""))
    }

    "getJsonResponse should return index missing for 404" in new WithApplication(fakeApp) {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService(new Elasticsearch)
      autoCompletionService.getJsonResponse(404).toString() must beEqualTo("""{"status":"index is missing"}""")
    }
  }
}
