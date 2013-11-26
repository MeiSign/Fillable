package queries

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import esclient.HttpType

class IndexExistsQuerySpec extends Specification {

  "IndexExistsQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: IndexExistsQuery = new IndexExistsQuery("index", "type")
      query.getUrlAddon must beEqualTo("/index/type")
    }

    "return the correct http method" in new WithApplication {
      val query: IndexExistsQuery = new IndexExistsQuery("index", "type")
      query.httpType must beEqualTo(HttpType.head)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: IndexExistsQuery = new IndexExistsQuery("index", "type")
      query.toJson.toString() must beEqualTo("{}")
    }
  }
}
