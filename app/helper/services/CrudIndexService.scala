package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries.EditFillableIndexQuery
import org.elasticsearch.indices.IndexMissingException

class CrudIndexService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def editFillableIndex(index: String, replicas: Int): Future[Int] = {
    EditFillableIndexQuery(esClient, index, replicas).execute map {
      editResponse => if (editResponse.isAcknowledged) 200 else 400
    } recover {
      case e: IndexMissingException => 404
    }
  }
}
