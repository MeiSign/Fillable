package esclient

import scala.collection.mutable
import org.elasticsearch.node.Node
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder._
import play.api.{Configuration, Play}

object NodeHolder {
  val nodes: mutable.ListBuffer[Node] = new mutable.ListBuffer[Node]()

  def buildNode = {
    val conf = Play.current.configuration
    val settings = buildSettings(conf)
    nodes += nodeBuilder().settings(settings).node()
  }

  def buildSettings(conf: Configuration) = {
    ImmutableSettings.settingsBuilder()
      .put("path.data", getDataPath(conf.getString("esnode.settings.data").getOrElse("data")))
      .put("http.enabled", conf.getBoolean("esnode.settings.httpEnabled").getOrElse(false))
      .put("local", conf.getBoolean("esnode.settings.local").getOrElse(true))
      .put("client", conf.getBoolean("esnode.settings.testnode").getOrElse(false))
      .put("cluster.name", conf.getString("esclient.clustername").getOrElse("fillable_es"))
  }

  def buildNodeWithSettings(settings: ImmutableSettings.Builder) = {
    nodes += nodeBuilder()
      .settings(settings)
      .clusterName(settings.get("cluster.name"))
      .client(settings.get("client").toBoolean)
      .data(!settings.get("client").toBoolean)
      .node()
  }

  def shutDownNodes() = nodes map(node => node.close())

  def getDataPath(pathConfig: String) = {
    if (!pathConfig.startsWith("/")) Play.current.getFile("/") + pathConfig
    else pathConfig
  }
}
