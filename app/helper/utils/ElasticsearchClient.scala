package helper.utils

import org.elasticsearch.node.Node
import org.elasticsearch.client.Client
import org.elasticsearch.node.NodeBuilder._

object ElasticsearchClient {
  val node: Node = getElasticNode
  val elasticClient: Client = node.client()

  def getElasticNode: Node = {
    nodeBuilder().clusterName("flb_cluster").client(false).node()
    //TODO Insert Settings here and maybe Transportclient for external Elastic Cluster
  }

  def shutdown: Unit = {
    node.close()
  }

  def warmer: Unit = {}
}
