package queries


import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import esclient.queries.{EditFillableIndexQuery, DeleteFillableIndexQuery, CreateFillableIndexQuery}
import esclient.HttpType

@RunWith(classOf[JUnitRunner])
class FillableIndexCrudQueriesSpec extends Specification {

  "CreateFillableIndexQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: CreateFillableIndexQuery= new CreateFillableIndexQuery("name", 4, 0)
      query.getUrlAddon must beEqualTo("/fbl_name")
    }

    "return the correct http method" in new WithApplication {
      val query: CreateFillableIndexQuery= new CreateFillableIndexQuery("name", 4, 0)
      query.httpType must beEqualTo(HttpType.Post)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: CreateFillableIndexQuery= new CreateFillableIndexQuery("name", 4, 0)
      query.toJson.toString() must beEqualTo("{\"settings\":{\"number_of_shards\":4,\"number_of_replicas\":0},\"mappings\":{\"fbl_name\":{\"properties\":{\"fillableOptions\":{\"type\":\"completion\"}}}}}")
    }
  }

  "DeleteFillableIndexQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: DeleteFillableIndexQuery= new DeleteFillableIndexQuery("name")
      query.getUrlAddon must beEqualTo("/name")
    }

    "return the correct http method" in new WithApplication {
      val query: DeleteFillableIndexQuery= new DeleteFillableIndexQuery("name")
      query.httpType must beEqualTo(HttpType.Delete)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: DeleteFillableIndexQuery= new DeleteFillableIndexQuery("name")
      query.toJson.toString() must beEqualTo("{}")
    }
  }

  "EditFillableIndexQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: EditFillableIndexQuery = new EditFillableIndexQuery("name", 4)
      query.getUrlAddon must beEqualTo("/name/_settings")
    }

    "return the correct http method" in new WithApplication {
      val query: EditFillableIndexQuery = new EditFillableIndexQuery("name", 4)
      query.httpType must beEqualTo(HttpType.Put)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: EditFillableIndexQuery = new EditFillableIndexQuery("name", 4)
      query.toJson.toString() must beEqualTo("{\"index\":{\"number_of_replicas\":4}}")
    }
  }
}
