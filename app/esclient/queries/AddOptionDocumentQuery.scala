package esclient.queries

import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client
import org.elasticsearch.action.index.IndexResponse

case class AddOptionDocumentQuery(esClient: Client, indexName: String, docIdString: String, doc: String) {
  lazy val p = promise[IndexResponse]()
  esClient
    .prepareIndex(indexName, indexName, docIdString.hashCode.toString)
    .setSource(doc)
    .execute()
    .addListener(new ActionListener[IndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: IndexResponse) = p success response
  })

  def execute: Future[IndexResponse] = p.future
}