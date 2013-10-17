import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import esclient.EsClient

@RunWith(classOf[JUnitRunner])
class EsClientSpec extends Specification {

  "EsClient" should {

    "return correct elasticsearch url from config" in new WithApplication {
      EsClient.url must beEqualTo(Play.current.configuration.getString("esclient.url").getOrElse(""))
    }
  }
}
