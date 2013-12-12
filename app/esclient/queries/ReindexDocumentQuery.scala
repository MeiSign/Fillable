package esclient.queries

import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client
import org.elasticsearch.action.update.UpdateResponse

case class ReindexDocumentQuery(esClient: Client, index: String, esType: String, id: String) {
  lazy val p = promise[UpdateResponse]()

  esClient
    .prepareUpdate(index, esType, id)
    .setDoc("{}")
    .execute()
    .addListener(new ActionListener[UpdateResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: UpdateResponse) = p success response
  })

  def execute: Future[UpdateResponse] = p.future
}