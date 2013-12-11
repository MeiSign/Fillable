package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse



case class GetFillableIndexQuery(esClient: Client) {
  lazy val p = promise[ClusterStateResponse]()
  val statement = esClient
    .admin()
    .cluster()
    .prepareState()
    .execute()
    .addListener(new ActionListener[ClusterStateResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: ClusterStateResponse) = p success response
  })

  def execute: Future[ClusterStateResponse] = p.future
}