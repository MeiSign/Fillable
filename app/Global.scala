import esclient.ElasticsearchClient
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    ElasticsearchClient.warmer
  }

  override def onStop(app: Application) {
    ElasticsearchClient.shutdown
  }
}
