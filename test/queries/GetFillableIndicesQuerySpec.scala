package queries

import org.specs2.mutable._

import play.api.test.WithApplication
import esclient.queries.{GetFillableIndicesQuery, GetFillableIndexQuery}
import esclient.HttpType

class GetFillableIndicesQuerySpec extends Specification {

  "GetFillableIndicesQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: GetFillableIndicesQuery = new GetFillableIndicesQuery()
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/_search")
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
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/3373707/_source")
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
