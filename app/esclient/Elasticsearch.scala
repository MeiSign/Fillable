package esclient

import org.elasticsearch.client.Client
import play.api.Play

class Elasticsearch {
  val client: Client = getClient

  def getClient: Client = {
    val embeddedElasticsearch = Play.current.configuration.getBoolean("esclient.embeddedElasticsearch").getOrElse(true)

    if (embeddedElasticsearch) NodeHolder.nodes.head.client()
    else TransportClientHolder.transportClient.head
  }

  def closeClient(): Unit = {
    client.close()
  }
}
