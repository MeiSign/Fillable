import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import esclient.EsClient
import esclient.queries.FindCompletionsQuery
import org.specs2.matcher.BeEqualTo
import play.api.libs.json._
import esclient.HttpType
import org.specs2.mock._
import esclient.queries.CreateCompletionsFieldQuery

@RunWith(classOf[JUnitRunner])
class CreateCompletionsFieldQuerySpec extends Specification {

  "CreateCompletionsFieldQuery Constructor" should {

    "throw an Illegal Argument Exception for a null indexName text" in new WithApplication {
      new CreateCompletionsFieldQuery(null) must throwA[IllegalArgumentException]
    }
    
    "getUrlAddon must return the correct url Addon" in new WithApplication {
      new CreateCompletionsFieldQuery("index").getUrlAddon must beEqualTo("/index")
    }
    
    "toJson should return the correct Json object" in new WithApplication {
      new CreateCompletionsFieldQuery("indexName").toJson must beEqualTo(
        Json.parse("""{"mappings":{"indexName":{"properties":{"suggest":{"type":"completion"},"searchId":{"type":"string"}}}}}""")
      )
    }
    
    "query must be of http type put" in new WithApplication {
      new CreateCompletionsFieldQuery("test").httpType must beEqualTo(HttpType.Put)
    }
  }
  
  "CreateCompletionsFieldQuery getResult" should {
    "throw an Illegal Argument Exception for a null response getResult call" in new WithApplication {
      new CreateCompletionsFieldQuery("Test").getResult(null) must throwA[IllegalArgumentException]
    }
    
    "should return an error object for an error response" in new WithApplication {
      val response: JsValue = Json.parse("""{"error" : "bla"}""")
      new CreateCompletionsFieldQuery("Test").getResult(response) must beEqualTo(Json.parse("""{"status":"error", "msg" : "bla"}""").as[JsObject])
    }
    
    "should return an empty success object for a valid json response" in new WithApplication {
      val response: JsValue = Json.parse("""{"ok":true,"acknowledged":true}""")
      new CreateCompletionsFieldQuery("Test").getResult(response) must beEqualTo(Json.parse("""{"status":"ok"}""").as[JsObject])
    }
  }
  
}
