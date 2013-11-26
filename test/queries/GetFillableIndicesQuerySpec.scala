package queries

import org.specs2.mutable._

import play.api.test.WithApplication
import esclient.{HttpType}
import esclient.queries.{GetFillableIndexQuery, GetFillableIndicesQuery}

class GetFillableIndicesQuerySpec extends Specification {

  "GetFillableIndicesQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: GetFillableIndicesQuery = new GetFillableIndicesQuery()
      query.getUrlAddon must beEqualTo("/_stats")
    }

    "return the correct http method" in new WithApplication {
      val query: GetFillableIndicesQuery = new GetFillableIndicesQuery()
      query.httpType must beEqualTo(HttpType.get)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: GetFillableIndicesQuery = new GetFillableIndicesQuery()
      query.toJson.toString() must beEqualTo("{}")
    }
  }

  "GetFillableIndex" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: GetFillableIndexQuery = new GetFillableIndexQuery("name")
      query.getUrlAddon must beEqualTo("/name/_settings")
    }

    "return the correct http method" in new WithApplication {
      val query: GetFillableIndexQuery = new GetFillableIndexQuery("name")
      query.httpType must beEqualTo(HttpType.get)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: GetFillableIndexQuery = new GetFillableIndexQuery("name")
      query.toJson.toString() must beEqualTo("{}")
    }
  }
}
