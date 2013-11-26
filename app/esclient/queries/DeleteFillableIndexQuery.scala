package esclient.queries

import org.elasticsearch.client.Client
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.ActionListener
import scala.concurrent._

case class DeleteFillableIndexQuery(esClient: Client, name: String) {
  lazy val p = promise[DeleteIndexResponse]()

  esClient
    .admin()
    .indices()
    .prepareDelete(name)
    .execute()
    .addListener(new ActionListener[DeleteIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: DeleteIndexResponse) = p success response
  })

  def execute: Future[DeleteIndexResponse] = p.future
}
