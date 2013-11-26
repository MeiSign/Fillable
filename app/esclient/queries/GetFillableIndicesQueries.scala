package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import play.api.libs.json.JsObject
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse

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

case class GetFillableIndexQuery(esClient: Client, index: String) extends EsQuery {
  lazy val p = promise[ClusterStateResponse]()
  esClient
    .admin()
    .cluster()
    .prepareState()
    .execute()
    .addListener(new ActionListener[ClusterStateResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: ClusterStateResponse) = p success response
  })

  def execute: Future[ClusterStateResponse] = p.future



  val httpType: HttpType.Value = HttpType.get

  def getUrlAddon: String = "/" + index + "/_settings"

  def toJson: JsObject = Json.obj()
}