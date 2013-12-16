package esclient

import play.api.{Configuration, Play}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.ImmutableSettings
import collection.JavaConversions._
import scala.collection.mutable

object TransportClientHolder {
  val transportClient: mutable.ListBuffer[TransportClient] = new mutable.ListBuffer[TransportClient]()

  def buildTransportClient(): Unit = {
    val conf = Play.current.configuration
    val hosts: List[String] = getHostList(conf)
    val settings = buildSettings(conf)

    transportClient += hosts.foldLeft(new TransportClient(settings)) {
      (client, host) => {
        client.addTransportAddress(new InetSocketTransportAddress(host.split(":")(0), host.split(":")(1).toInt))
      }
    }
  }

  def buildSettings(conf: Configuration) = {
    ImmutableSettings.settingsBuilder()
      .put("cluster.name", conf.getString("esclient.clustername").getOrElse("fillable_es"))
      .put("client.transport.sniff", true)
      .build()
  }

  def getHostList(conf: Configuration) = conf.getStringList("esclient.transportClientUrls").map(_.toList).getOrElse(List())

  def shutdownTransportClient() = transportClient map(client => client.close())
}
