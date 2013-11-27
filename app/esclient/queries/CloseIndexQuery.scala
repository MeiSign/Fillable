package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse

case class CloseIndexQuery(esClient: Client, index: String) {
  lazy val p = promise[CloseIndexResponse]()

  esClient
    .admin()
    .indices()
    .prepareClose(index)
    .execute()
    .addListener(new ActionListener[CloseIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: CloseIndexResponse) = p success response
  })

  def execute: Future[CloseIndexResponse] = p.future
}
