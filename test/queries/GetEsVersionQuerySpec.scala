package queries

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import esclient.{HttpType}
import esclient.queries.GetEsVersionQuery

class GetEsVersionQuerySpec extends Specification {

  "GetEsVersionQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: GetEsVersionQuery = new GetEsVersionQuery()
      query.getUrlAddon must beEqualTo("/")
    }

    "return the correct http method" in new WithApplication {
      val query: GetEsVersionQuery = new GetEsVersionQuery()
      query.httpType must beEqualTo(HttpType.get)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: GetEsVersionQuery = new GetEsVersionQuery()
      query.toJson.toString() must beEqualTo("{}")
    }
  }
}
