package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries.{DeleteFillableIndexQuery, EditFillableIndexQuery}
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

  def deleteFillableIndex(esClient: Client, index: String): Future[Int] = {
    if (!index.startsWith("fbl_")) Future.successful(404)
    else {
      DeleteFillableIndexQuery(esClient, index).execute map {
        deleteResponse => if (deleteResponse.isAcknowledged) 200 else 400
      } recover {
        case e: IndexMissingException => 404
      }
    }
  }
}
