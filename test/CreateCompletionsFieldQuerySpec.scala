import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import esclient.EsClient
import esclient.queries.GetSuggestionsQuery
import org.specs2.matcher.BeEqualTo
import play.api.libs.json._
import esclient.HttpType
import esclient.queries.CreateSuggestionIndexQuery
import play.api.libs.ws.Response
import org.specs2.mock._

@RunWith(classOf[JUnitRunner])
class CreateCompletionsFieldQuerySpec extends Specification with Mockito {

  "CreateCompletionsFieldQuery Constructor" should {

    "throw an Illegal Argument Exception for a null indexName text" in new WithApplication {
      new CreateSuggestionIndexQuery(null) must throwA[IllegalArgumentException]
    }
    
    "getUrlAddon must return the correct url Addon" in new WithApplication {
      new CreateSuggestionIndexQuery("index").getUrlAddon must beEqualTo("/index")
    }
    
    "toJson should return the correct Json object" in new WithApplication {
      new CreateSuggestionIndexQuery("indexName").toJson must beEqualTo(
        Json.parse("""{"mappings":{"indexName":{"properties":{"suggest":{"type":"completion"}}}}}""")
      )
    }
    
    "query must be of http type put" in new WithApplication {
      new CreateSuggestionIndexQuery("test").httpType must beEqualTo(HttpType.Put)
    }
  }
  
  "CreateSuggestionIndexQuery getResult" should {
    "throw an Illegal Argument Exception for a null response getResult call" in new WithApplication {
      new CreateSuggestionIndexQuery("Test").getJsResult(null) must throwA[IllegalArgumentException]
    }
    
    "should return an error object for an error response" in {
      val response = mock[Response]
      response.json returns Json.parse("""{"error" : "bla"}""")
      new CreateSuggestionIndexQuery("Test").getJsResult(response) must beEqualTo(Json.parse("""{"status":"error", "msg" : "bla"}""").as[JsObject])
    }
    
    "should return an empty success object for a valid json response" in {
      
      val response = mock[Response]
      response.json returns Json.parse("""{"ok":true,"acknowledged":true}""")
      new CreateSuggestionIndexQuery("Test").getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok"}""").as[JsObject])
    }
  }
  
}
