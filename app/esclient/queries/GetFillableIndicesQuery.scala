package esclient.queries

import org.elasticsearch.client.Client
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.ActionListener
import scala.concurrent._

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
