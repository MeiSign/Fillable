package queries


import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import esclient.queries._
import esclient.HttpType

@RunWith(classOf[JUnitRunner])
class FillableIndexRegisterQueriesSpec extends Specification {

  "FillableIndexRegisterQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: FillableIndexRegisterQuery = new FillableIndexRegisterQuery("name", 4, 0)
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/" + "fbl_name".hashCode)
    }

    "return the correct url addon for a valid query object with upper case characters in indexname" in new WithApplication {
      val query: FillableIndexRegisterQuery = new FillableIndexRegisterQuery("NAME", 4, 0)
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/" + "fbl_name".hashCode)
    }

    "return the correct http method" in new WithApplication {
      val query: FillableIndexRegisterQuery = new FillableIndexRegisterQuery("name", 4, 0)
      query.httpType must beEqualTo(HttpType.post)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: FillableIndexRegisterQuery= new FillableIndexRegisterQuery("name", 4, 0)
      query.toJson.toString() must beEqualTo("{\"name\":\"fbl_name\",\"shards\":4,\"replicas\":0}")
    }
  }

  "FillableIndexReregisterQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: FillableIndexReregisterQuery= new FillableIndexReregisterQuery("name", 4, 0)
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/" + "name".hashCode)
    }

    "return the correct http method" in new WithApplication {
      val query: FillableIndexReregisterQuery= new FillableIndexReregisterQuery("name", 4, 0)
      query.httpType must beEqualTo(HttpType.put)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: FillableIndexReregisterQuery= new FillableIndexReregisterQuery("name", 4, 0)
      query.toJson.toString() must beEqualTo("{\"name\":\"name\",\"shards\":4,\"replicas\":0}")
    }
  }

  "FillableIndexUnregisterQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: FillableIndexUnregisterQuery = new FillableIndexUnregisterQuery("name")
      query.getUrlAddon must beEqualTo("/fbl_indices/indices/" + "name".hashCode)
    }

    "return the correct http method" in new WithApplication {
      val query: FillableIndexUnregisterQuery = new FillableIndexUnregisterQuery("name")
      query.httpType must beEqualTo(HttpType.delete)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: FillableIndexUnregisterQuery = new FillableIndexUnregisterQuery("name")
      query.toJson.toString() must beEqualTo("{}")
    }
  }

  "FillableIndexSetupQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: FillableIndexSetupQuery = new FillableIndexSetupQuery()
      query.getUrlAddon must beEqualTo("/fbl_indices")
    }

    "return the correct http method" in new WithApplication {
      val query: FillableIndexSetupQuery = new FillableIndexSetupQuery()
      query.httpType must beEqualTo(HttpType.post)
    }

    "return the correct json object for a valid query object" in new WithApplication {
      val query: FillableIndexSetupQuery = new FillableIndexSetupQuery()
      query.toJson.toString() must beEqualTo("{\"settings\":{\"number_of_shards\":1,\"number_of_replicas\":0},\"mappings\":{\"indices\":{\"properties\":{\"name\":{\"type\":\"String\"},\"shards\":{\"type\":\"integer\"},\"replicas\":{\"type\":\"integer\"}}}}}")
    }
  }
}
