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
import org.specs2.mock._
import esclient.queries.GetEsVersionQuery
import play.api.libs.ws.Response

@RunWith(classOf[JUnitRunner])
class GetEsVersionQuerySpec extends Specification with Mockito {
  
  "GetEsVersionQuery getResult" should {
    "respondSuccess with false if Elasticsearch version is too old(older than 0.90.5)" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "version" : { "number" : "0.90.5", "build_hash" : "c8714e8e0620b62638f660f6144831792b9dedee", "build_timestamp" : "2013-09-17T12:20:20Z", "build_snapshot" : false, "lucene_version" : "4.4" }, "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","version":"0.90.5","fullfillsRequirements":false}"""))
    }
    "respondSuccess with true if Elasticsearch version is newer than 0.90.5" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "version" : { "number" : "0.90.5", "build_hash" : "c8714e8e0620b62638f660f6144831792b9dedee", "build_timestamp" : "2013-09-18T12:50:20Z", "build_snapshot" : false, "lucene_version" : "4.4" }, "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","version":"0.90.5","fullfillsRequirements":true}"""))
    }
    "respondSuccess with true if Elasticsearch version is the same" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "version" : { "number" : "0.90.5", "build_hash" : "c8714e8e0620b62638f660f6144831792b9dedee", "build_timestamp" : "2013-09-17T12:50:20Z", "build_snapshot" : false, "lucene_version" : "4.4" }, "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","version":"0.90.5","fullfillsRequirements":true}"""))
    }
    "respondSuccess with correct version from Elasticsearch answer" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "version" : { "number" : "0.20.15", "build_hash" : "c8714e8e0620b62638f660f6144831792b9dedee", "build_timestamp" : "2013-09-17T12:50:20Z", "build_snapshot" : false, "lucene_version" : "4.4" }, "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"ok","version":"0.20.15","fullfillsRequirements":true}"""))
    }
    "respondError with correct error msg for invalid DateTimeFormat" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "version" : { "number" : "0.20.15", "build_hash" : "c8714e8e0620b62638f660f6144831792b9dedee", "build_timestamp" : "2013-09-17", "build_snapshot" : false, "lucene_version" : "4.4" }, "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"error","msg":"Elasticsearch responded with a wrong DateTime Format(expected: yyyy-MM-dd'T'HH:mm:ss'Z' received: 2013-09-17). Anyways, if your Elasticsearch version is above 0.90.5 there should not be any compatibility issues."}"""))
    }
    "respondError with correct error msg if es version is not accessible" in new WithApplication {
      val response = mock[Response]
      response.json returns Json.parse("""{"ok" : true, "status" : 200, "name" : "Payge, Reeva", "tagline" : "You Know, for Search" }""")
      new GetEsVersionQuery().getJsResult(response) must beEqualTo(Json.parse("""{"status":"error","msg":"Could not determine elasticsearch version."}"""))
    }
  }  
}