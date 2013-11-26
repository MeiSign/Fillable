package esclient

import org.elasticsearch.node.Node
import org.elasticsearch.client.Client
import org.elasticsearch.node.NodeBuilder._
import org.elasticsearch.common.settings.ImmutableSettings

object ElasticsearchClient {
  val settings = ImmutableSettings.settingsBuilder()
    .put("path.data", "data")
    .put("http.enabled", false)
  val node: Node = nodeBuilder().settings(settings).clusterName("fbl_cluster").data(true).node()
  val elasticClient: Client = node.client()

  def shutdown: Unit = {
    node.close()
  }

  def warmer: Unit = {

  }
}
