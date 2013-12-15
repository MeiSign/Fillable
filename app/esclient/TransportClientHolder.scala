package esclient

import org.elasticsearch.client.Client
import play.api.{Configuration, Play}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.ImmutableSettings
import collection.JavaConversions._

object TransportClientHolder {
  def buildTransportClient(): Client = {
    val conf = Play.current.configuration
    val hosts: List[String] = getHostList(conf)
    val settings = buildSettings(conf)

    hosts.foldLeft(new TransportClient(settings)) {
      (client, host) => {
        client.addTransportAddress(new InetSocketTransportAddress(host.split(":")(0), host.split(":")(1).toInt))
      }
    }
  }

  def buildSettings(conf: Configuration) = {
    ImmutableSettings.settingsBuilder()
      .put("cluster.name", conf.getString("esclient.clustername").getOrElse("fillable_es"))
      .build()
  }

  def getHostList(conf: Configuration) = conf.getStringList("esclient.transportClientUrls").map(_.toList).getOrElse(List())
}
