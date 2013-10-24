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
    "respondError should report a valid error object" in new WithApplication {
      EsQuery.respondWithError("errormsg") must beEqualTo(Json.obj("status" -> "error", "msg" -> "errormsg"))
    }
    "respondSuccess should report a valid object" in new WithApplication {
      val testObj: JsObject = Json.obj("bla" -> "blub");
      EsQuery.respondWithSuccess(testObj) must beEqualTo(Json.obj("status" -> "ok") ++ testObj)
    }
  }  
}
