package queries

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import esclient.queries.AddOptionDocumentQuery
import models.OptionDocument
import esclient.HttpType

@RunWith(classOf[JUnitRunner])
class AddOptionDocumentQuerySpec extends Specification {

  "AddOptionDocumentQuery" should  {
    "return the correct url addon for a valid query object" in new WithApplication {
      val query: AddOptionDocumentQuery = new AddOptionDocumentQuery("index", "id", new OptionDocument())
      query.getUrlAddon must beEqualTo("/index/index/" + "id".hashCode)
    }

    "return the correct http method" in new WithApplication {
      val query: AddOptionDocumentQuery = new AddOptionDocumentQuery("index", "id", new OptionDocument())
      query.httpType must beEqualTo(HttpType.put)
    }

    "return the correct Json object for a valid query object" in new WithApplication {
      val query: AddOptionDocumentQuery = new AddOptionDocumentQuery("index", "id", new OptionDocument())
      query.toJson.toString() must beEqualTo("{\"fillableOptions\":{\"input\":[],\"output\":\"\",\"weight\":0}}")
    }


  }
}

