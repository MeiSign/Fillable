package queries

import org.specs2.mutable._

import play.api.test._
import esclient.queries._
import esclient.HttpType

class GetOptionsQuerySpec extends Specification {

  "GetOptionsQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: GetOptionsQuery = new GetOptionsQuery("index", "bana")
      query.getUrlAddon must beEqualTo("/index/_suggest")
    }

    "return the correct http method" in new WithApplication {
      val query: GetOptionsQuery = new GetOptionsQuery("index", "bana")
      query.httpType must beEqualTo(HttpType.post)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: GetOptionsQuery = new GetOptionsQuery("index", "bana")
      query.toJson.toString() must beEqualTo("{\"index\":{\"text\":\"bana\",\"completion\":{\"field\":\"fillableOptions\",\"fuzzy\":{\"edit_distance\":1}}}}")
    }

  }
}
