package esclient

import scala.collection.mutable
import org.elasticsearch.node.Node
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder._
import play.api.Play

object NodeHolder {
  val nodes: mutable.ListBuffer[Node] = new mutable.ListBuffer[Node]()

  def buildNode = {
    val conf = Play.current.configuration
    val nodeSettings = ImmutableSettings.settingsBuilder()
      .put("path.data", conf.getString("esnode.settings.data").getOrElse("data"))
      .put("http.enabled", conf.getBoolean("esnode.settings.httpEnabled").getOrElse(false))
      .put("local", conf.getBoolean("esnode.settings.local").getOrElse(true))
    nodes += nodeBuilder()
      .settings(nodeSettings)
      .clusterName(conf.getString("esclient.clustername").getOrElse("fbl_cluster"))
      .node()
  }

  def buildNode(settings: ImmutableSettings.Builder) = {
    nodes += nodeBuilder().settings(settings).clusterName(settings.get("cluster.name")).node()
  }

  def shutDownNodes() = nodes map(node => node.close())
}
