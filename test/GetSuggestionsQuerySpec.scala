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
import org.specs2.mock._
import esclient.queries.GetSuggestionsQuery
import play.api.libs.ws.Response

@RunWith(classOf[JUnitRunner])
class GetSuggestionsQuerySpec extends Specification with Mockito {

  "GetSuggestionsQuery Constructor" should {

    "throw an Illegal Argument Exception for a null toBeCompleted text" in new WithApplication {
      new GetSuggestionsQuery("test", null) must throwA[IllegalArgumentException]
    }
    
    "throw an Illegal Argument Exception for a null indexName text" in new WithApplication {
      new GetSuggestionsQuery(null, "test") must throwA[IllegalArgumentException]
    }
    
    "getUrlAddon must return the correct url Addon" in new WithApplication {
      new GetSuggestionsQuery("index", "test").getUrlAddon must beEqualTo("/index/_suggest")
    }
    
    "toJson should return the correct Json object" in new WithApplication {
      new GetSuggestionsQuery("indexName", "text").toJson must beEqualTo(
        Json.obj(
          "indexName" -> Json.obj(
            "text" -> "text",
            "completion" -> Json.obj("field" -> "suggest", "fuzzy" -> Json.obj("edit_distance" -> 1))
          )
        )
      )
    }
    
    "query must be of http type post" in new WithApplication {
      new GetSuggestionsQuery("test", "index").httpType must beEqualTo(HttpType.Post)
    }
  }
  
  "GetSuggestionsQuery getResult" should {
    "throw an Illegal Argument Exception for a null response getResult call" in new WithApplication {
      new GetSuggestionsQuery("Test", "test").getJsResult(null) must throwA[IllegalArgumentException]
    }
    
    "should return the correct completions for a valid json response" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0},"hotels":[{"text":"m","offset":0,"length":1,"options":[{"text":"Hotel Marriot","score":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","score":5.0}]}]}""")
      new GetSuggestionsQuery("Test", "test").getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","completions":[{"text":"Hotel Marriot","score":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","score":5.0}]}""").as[JsObject])
    }
    
    "should return an error object for invalid json responses" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"error" : "bla"}""")
      new GetSuggestionsQuery("Test", "test").getJsResult(response) must beEqualTo(Json.parse("""{"status":"error", "msg" : "bla"}""").as[JsObject])
    }
    
    "should return an empty result for a valid empty json response" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0},"hotels":[{"text":"m","offset":0,"length":1,"options":[]}]}""")
      new GetSuggestionsQuery("Test", "test").getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","completions":[]}""").as[JsObject])
    }
  }
  
}
