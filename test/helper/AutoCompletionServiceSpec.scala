package helper

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import org.specs2.mock.Mockito
import play.api.libs.json.Json

class AutoCompletionServiceSpec extends Specification with Mockito {

  "AutoCompletionService" should {
    "getDocumentId should return typed String for empty chosen option" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getDocumentId("typed", Option.empty) must beEqualTo("typed".hashCode.toString)
    }

    "getDocumentId should return typed String for empty chosen string" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getDocumentId("typed", Option("")) must beEqualTo("typed".hashCode.toString)
    }

    "getDocumentId should return chosen String for empty typed string" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getDocumentId("", Option("chosen")) must beEqualTo("chosen".hashCode.toString)
    }

    "addOption should return 400 Bad Request statuscode for empty index parameter" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.addOption("", Option("typed"), Option("Chosen")) must beEqualTo(400).await
    }

    "addOption should return 204 Success statuscode for empty type parameter" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.addOption("index", Option.empty, Option("Chosen")) must beEqualTo(204).await
    }

    "addOption should return 204 Success statuscode for empty string as type parameter" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.addOption("index", Option(""), Option("Chosen")) must beEqualTo(204).await
    }

    "addOption should return 200 Success statuscode if option will be created" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      //autoCompletionService.parseDocument(Json.obj()) returns (new OptionDocument(List(), "", 0))
      autoCompletionService.addOption("index", Option(""), Option("Chosen")) must beEqualTo(200).await
    }

    "addOption should return 202 Success statuscode if option will be extended" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      //autoCompletionService.parseDocument(Json.obj()) returns (new OptionDocument(List("bla"), "blub", 1))
      autoCompletionService.addOption("index", Option(""), Option("Chosen")) must beEqualTo(202).await
    }

    "getOption should return empty js array for empty index parameter" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getOptions("", "toBeCompleted") must beEqualTo(Json.arr()).await
    }

    "getOption should return empty js array for empty toBeCompleted parameter" in new WithApplication {
      val autoCompletionService: AutoCompletionService = new AutoCompletionService
      autoCompletionService.getOptions("index", "") must beEqualTo(Json.arr()).await
    }
  }

}
