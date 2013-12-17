package esclient.queries

import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.action.bulk.BulkResponse

case class ReindexDocumentsBulkQuery(esClient: Client, index: String, esType: String, completions: Array[String]) {
  lazy val p = promise[BulkResponse]()

  val bulkRequest = completions.foldLeft(esClient.prepareBulk()) {
    (bulkRequest, completion) => {
      bulkRequest.add(
        esClient
        .prepareUpdate(index, esType, completion.hashCode.toString)
        .setDoc("{}")
      )
    }
  }

  bulkRequest.execute().addListener(new ActionListener[BulkResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: BulkResponse) = p success response
  })

  def execute: Future[BulkResponse] = p.future
}