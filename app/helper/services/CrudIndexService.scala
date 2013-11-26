package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import org.elasticsearch.indices.{IndexAlreadyExistsException, IndexMissingException}
import esclient.queries.{EditFillableIndexQuery, DeleteFillableIndexQuery, CreateFillableIndexQuery}

class CrudIndexService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def editFillableIndex(index: String, replicas: Int): Future[Int] = {
    EditFillableIndexQuery(esClient, index, replicas).execute map {
      editResponse => if (editResponse.isAcknowledged) 200 else 400
    } recover {
      case e: IndexMissingException => 404
      case _ => 400
    }
  }

  def deleteFillableIndex(esClient: Client, index: String): Future[Int] = {
    if (!index.startsWith("fbl_")) Future.successful(404)
    else {
      DeleteFillableIndexQuery(esClient, index).execute map {
        deleteResponse => if (deleteResponse.isAcknowledged) 200 else 400
      } recover {
        case e: IndexMissingException => 404
        case _ => 400
      }
    }
  }

  def createFillablendex(index: String, shards: Int, replicas: Int): Future[Int] = {
    CreateFillableIndexQuery(esClient, index, shards, replicas).execute map {
      createResponse => if (createResponse.isAcknowledged) 200 else 404
    } recover {
      case e: IndexAlreadyExistsException => 400
      case _ => 404
    }
  }
}
