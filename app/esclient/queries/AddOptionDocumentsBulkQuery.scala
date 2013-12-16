package esclient.queries

import org.elasticsearch.client.Client
import models.OptionDocument
import scala.concurrent._
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.ActionListener

case class AddOptionDocumentsBulkQuery(esClient: Client,indexName: String, docs: Map[String, OptionDocument]) {
  lazy val p = promise[BulkResponse]()

  val bulkRequest = docs.foldLeft(esClient.prepareBulk()) {
    (bulkRequest, doc) => {bulkRequest.add(esClient
      .prepareIndex(indexName, indexName, doc._1.hashCode.toString)
      .setSource(doc._2.toJsonBuilder))}
  }

  bulkRequest.execute().addListener(new ActionListener[BulkResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: BulkResponse) = p success response
  })

  def execute: Future[BulkResponse] = p.future
}
