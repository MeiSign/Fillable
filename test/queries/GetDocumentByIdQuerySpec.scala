package queries

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import esclient.{HttpType}
import esclient.queries.GetDocumentByIdQuery

@RunWith(classOf[JUnitRunner])
class GetDocumentByIdQuerySpec extends Specification {

  "GetDocumentById" should  {
       "return the correct url addon for a valid query object" in new WithApplication {
           val query: GetDocumentByIdQuery = new GetDocumentByIdQuery("index", "id")
           query.getUrlAddon must beEqualTo("/index/index/id")
       }

       "return the correct http method" in new WithApplication {
          val query: GetDocumentByIdQuery = new GetDocumentByIdQuery("index", "id")
          query.httpType must beEqualTo(HttpType.get)
       }
  }
}
