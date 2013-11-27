package esclient.queries

import scala.concurrent._
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client

case class GetDocumentByIdQuery(esClient: Client, indexName: String, typeName: String, id: String) {
  lazy val p = promise[GetResponse]()

  esClient
    .prepareGet(indexName, typeName, id)
    .execute()
    .addListener(new ActionListener[GetResponse] {

    def onFailure(e: Throwable) = {
      p failure e
    }

    def onResponse(response: GetResponse) = p success response
  })

  def execute: Future[GetResponse] = p.future
}
