import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import esclient.EsClient
import org.specs2.matcher.BeEqualTo
import play.api.libs.json._
import esclient.HttpType
import esclient.EsQuery

@RunWith(classOf[JUnitRunner])
class EsQueriesSpec extends Specification {
  
  "EsQuery" should {
    "use get as default http method" in {
      EsQuery.httpType must beEqualTo(HttpType.get)
    }
    "send an empty json object as default" in {
      EsQuery.toJson must beEqualTo(Json.obj())
    }
    "use an empty string as default urladdon" in {
      EsQuery.getUrlAddon must beEqualTo("")
    }
  }  
}
