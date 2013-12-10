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
  val node: Option[Node] = buildNode()
  val elasticClient: Client = buildClient(embeddedElasticsearch)

  def buildClient(embedded: Boolean): Client = {
    if (embeddedElasticsearch) buildNodeClient(node)
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

  def buildNode(): Option[Node] = {
    if (embeddedElasticsearch) {
      val settings = ImmutableSettings.settingsBuilder()
        .put("path.data", "data")
        .put("http.enabled", false)
      Option(nodeBuilder().settings(settings).clusterName("fbl_cluster").data(true).node())
    } else {
      None
    }
  }

  def buildNodeClient(node: Option[Node]): Client = {
    if (node.isDefined) node.get.client()
    else buildTransportClient()
  }

  def shutdown: Unit = {
    if (embeddedElasticsearch) node.get.close()
    else elasticClient.close()
  }
}
