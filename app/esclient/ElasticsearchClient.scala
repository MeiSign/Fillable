package esclient

import org.elasticsearch.node.Node
import org.elasticsearch.client.Client
import org.elasticsearch.node.NodeBuilder._
import org.elasticsearch.common.settings.ImmutableSettings
import play.api.Play
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import collection.JavaConversions._


object ElasticsearchClient {
  val embeddedElasticsearch = Play.current.configuration.getBoolean("esclient.embeddedElasticsearch").getOrElse(true)
  val elasticClient: Client = buildClient(embeddedElasticsearch)


  def buildClient(embedded: Boolean): Client = {
    if (embeddedElasticsearch) {
      buildNodeClient()
    } else {
      buildTransportClient()
    }
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

  def buildNodeClient(): Client = {
    val settings = ImmutableSettings.settingsBuilder()
      .put("path.data", "data")
      .put("http.enabled", false)
    val node: Node = nodeBuilder().settings(settings).clusterName("fbl_cluster").data(true).node()

    node.client()
  }

  def shutdown: Unit = {
    //node.close()
  }

  def warmer: Unit = {

  }
}
