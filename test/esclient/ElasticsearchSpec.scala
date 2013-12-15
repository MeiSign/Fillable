package esclient

import org.specs2.mutable.Specification
import play.api.test.{WithApplication, FakeApplication}
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient

class ElasticsearchSpec extends Specification {

  val nodeConfig: Map[String, _] = Map(
    "esclient.embeddedElasticsearch" -> true,
    "esnode.settings.data" -> "data",
    "esnode.settings.local" -> true,
    "esnode.settings.httpEnabled" -> true,
    "esnode.settings.testnode" -> true
  )

  val transportConfig: Map[String, _] = Map("esclient.embeddedElasticsearch" -> false)



  "Elasticsearch" should {

    "client should return a NodeClient if embeddedElasticsearch is activated in config" in new WithApplication(new FakeApplication(additionalConfiguration = nodeConfig)) {
     val client = new Elasticsearch().getClient
      (client.isInstanceOf[Client] && !client.isInstanceOf[TransportClient]) must beTrue
    }

    "client should return a Transportclient if embeddedElasticsearch is deactivated in config" in new WithApplication(new FakeApplication(additionalConfiguration = transportConfig)) {
      val client = new Elasticsearch().getClient
      client.isInstanceOf[TransportClient] must beTrue
    }
  }
}
