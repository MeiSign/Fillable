package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import play.api.libs.json.JsObject
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse

case class GetFillableIndicesQuery(esClient: Client) {
  lazy val p = promise[IndicesStatsResponse]()
  esClient
    .admin()
    .indices()
    .prepareStats()
    .clear()
    .all()
    .setStore(true)
    .execute()
    .addListener(new ActionListener[IndicesStatsResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: IndicesStatsResponse) = p success response
  })

  def execute: Future[IndicesStatsResponse] = p.future
}

case class GetFillableIndexQuery(index: String) extends EsQuery {
  
  val httpType: HttpType.Value = HttpType.get

  def getUrlAddon: String = "/" + index + "/_settings"

  def toJson: JsObject = Json.obj()
}