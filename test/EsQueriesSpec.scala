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

@RunWith(classOf[JUnitRunner])
class EsQueriesSpec extends Specification {

  "FindCompletionsQuery" should {

    "throw an Illegal Argument Exception for a null toBeCompleted text" in new WithApplication {
      new FindCompletionsQuery("test", "test", null) must throwA[IllegalArgumentException]
    }
    
    "throw an Illegal Argument Exception for a null fieldName text" in new WithApplication {
      new FindCompletionsQuery("test", null, "test") must throwA[IllegalArgumentException]
    }
    
    "throw an Illegal Argument Exception for a null indexName text" in new WithApplication {
      new FindCompletionsQuery(null, "test", "test") must throwA[IllegalArgumentException]
    }
    
    "getUrlAddon must return the correct url Addon" in new WithApplication {
      new FindCompletionsQuery("index", "test", "test").getUrlAddon must beEqualTo("/index/_suggest")
    }
    
    "toJson should return the correct Json object" in new WithApplication {
      new FindCompletionsQuery("indexName", "fieldName", "text").toJson must beEqualTo(
        Json.obj(
          "indexName" -> Json.obj(
            "text" -> "text",
            "completion" -> Json.obj("field" -> "fieldName")
          )
        )
      )
    }
    
    "query must be of http type post" in new WithApplication {
      new FindCompletionsQuery("test", "test", "index").httpType must beEqualTo(HttpType.Post)
    }
  }
}
