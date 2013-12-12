package esclient

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import play.api.Play
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import collection.JavaConversions._

class Elasticsearch {
  val embeddedElasticsearch = Play.current.configuration.getBoolean("esclient.embeddedElasticsearch").getOrElse(true)
  val client: Client = buildClient(embeddedElasticsearch)

  def buildClient(embedded: Boolean): Client = {
    if (embeddedElasticsearch) NodeHolder.nodes.head.client()
    else buildTransportClient()
  }

  def buildTransportClient(): Client = {
    val adresses: List[String] = Play.current.configuration.getStringList("esclient.transportClientUrls").map(_.toList).getOrElse(List())
    val client = new TransportClient(ImmutableSettings.settingsBuilder().build())
    val clients = adresses.map {
      adress => {
        client.addTransportAddress(new InetSocketTransportAddress(adress.split(":")(0), adress.split(":")(1).toInt))
      }
    }

    clients.reverse.head
  }

  def closeClient(): Unit = {
    client.close()
  }
}
