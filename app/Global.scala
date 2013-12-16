import org.elasticsearch.client.transport.NoNodeAvailableException
import play.api._
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import scala.concurrent.Future
import views.html
import collection.JavaConversions._
import esclient.{TransportClientHolder, NodeHolder}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val embeddedElasticsearch = app.configuration.getBoolean("esclient.embeddedElasticsearch").getOrElse(true)
    if (embeddedElasticsearch) NodeHolder.buildNode
    else TransportClientHolder.buildTransportClient()
  }

  override def onStop(app: Application) {
    val embeddedElasticsearch = app.configuration.getBoolean("esclient.embeddedElasticsearch").getOrElse(true)
    if (embeddedElasticsearch) NodeHolder.shutDownNodes()
    else TransportClientHolder.shutdownTransportClient()
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
      ex match {
        case ex if ex.getCause().isInstanceOf[NoNodeAvailableException] => {
          val embedded: Option[String] = Play.current.configuration.getString("esclient.embeddedElasticsearch")
          val transportUrls: List[String] = Play.current.configuration.getStringList("esclient.transportClientUrls").map(_.toList).getOrElse(List())
          Future.successful(Ok(html.status.noEsConnection(embedded.getOrElse(""), "[" + transportUrls.mkString(", ") + "]")))
        }
        case _ => super.onError(request, ex)
      }
  }
}
