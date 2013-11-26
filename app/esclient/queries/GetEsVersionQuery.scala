package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse

case class GetEsVersionQuery(esClient: Client) {
  lazy val p = promise[NodesInfoResponse]()

  esClient
    .admin()
    .cluster()
    .prepareNodesInfo()
    .all()
    .execute()
    .addListener(new ActionListener[NodesInfoResponse] {

    def onFailure(e: Throwable) = {
      p failure e
    }

    def onResponse(response: NodesInfoResponse) = p success response
  })

  def execute: Future[NodesInfoResponse] = p.future
}