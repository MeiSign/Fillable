import esclient.ElasticsearchClient
import org.elasticsearch.client.transport.NoNodeAvailableException
import play.api._
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import scala.concurrent.Future
import views.html
import collection.JavaConversions._

object Global extends GlobalSettings {
  override def onStop(app: Application) {
    ElasticsearchClient.shutdown
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
      ex match {
        case ex if ex.getCause().isInstanceOf[NoNodeAvailableException] => {
          val embedded: Option[String] = Play.current.configuration.getString("esclient.embeddedElasticsearch")
          val transportUrls: List[String] = Play.current.configuration.getStringList("esclient.transportClientUrls").map(_.toList).getOrElse(List())
          Future.successful(Ok(html.status.noEsConnection(embedded.getOrElse(""), "[" + transportUrls.mkString(", ") + "]")))
        }
      }
  }
}
