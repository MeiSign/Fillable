import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{Json, JsObject}
import play.api.libs.ws.WS.WSRequestHolder
import play.api.test._
import collection.JavaConversions._
import play.api.Play
import esclient.{HttpType, EsQuery, EsClient}

@RunWith(classOf[JUnitRunner])
class EsClientSpec extends Specification with Mockito {
  "EsClient" should {

    "return correct elasticsearch url from config" in new WithApplication {
      EsClient.url(new TestGetQuery) must beEqualTo(Play.current.configuration.getStringList("esclient.url").map(_.toList).getOrElse(List())(0))
    }

    "executen of get query should call WS.get" in new WithApplication {
      val ws = mock[WSRequestHolder]
      val client: EsClient = new EsClient(Option(ws))
      val testQuery: TestGetQuery = new TestGetQuery
      client.execute(testQuery)

      there was one(ws).get()
    }

    "executen of head query should call WS.head" in new WithApplication {
      val ws = mock[WSRequestHolder]
      val client: EsClient = new EsClient(Option(ws))
      val testQuery: TestHeadQuery = new TestHeadQuery
      client.execute(testQuery)

      there was one(ws).head()
    }

    "executen of delete query should call WS.delete" in new WithApplication {
      val ws = mock[WSRequestHolder]
      val client: EsClient = new EsClient(Option(ws))
      val testQuery: TestDeleteQuery = new TestDeleteQuery
      client.execute(testQuery)

      there was one(ws).delete()
    }
  }
}

class TestGetQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.get
  def getUrlAddon: String = ""
  def toJson: JsObject = Json.obj()
}

class TestPostQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.post
  def getUrlAddon: String = ""
  def toJson: JsObject = Json.obj()
}

class TestPutQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.put
  def getUrlAddon: String = ""
  def toJson: JsObject = Json.obj()
}

class TestHeadQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.head
  def getUrlAddon: String = ""
  def toJson: JsObject = Json.obj()
}

class TestDeleteQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.delete
  def getUrlAddon: String = ""
  def toJson: JsObject = Json.obj()
}
