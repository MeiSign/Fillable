package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse

case class OpenIndexQuery(esClient: Client, index: String) {
  lazy val p = promise[OpenIndexResponse]()

  esClient
    .admin()
    .indices()
    .prepareOpen(index)
    .execute()
    .addListener(new ActionListener[OpenIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: OpenIndexResponse) = p success response
  })

  def execute: Future[OpenIndexResponse] = p.future
}
