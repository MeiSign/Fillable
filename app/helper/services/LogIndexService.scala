package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries.{CreateFillableLogIndexQuery, DeleteFillableIndexQuery}
import org.elasticsearch.indices.IndexAlreadyExistsException

class LogIndexService(esClient: Client) {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def deleteLogIndex(index: String): Future[Int] = {
    DeleteFillableIndexQuery(esClient, index).execute map {
      closeResponse => if (closeResponse.isAcknowledged) 200 else 404
    } recover {
      case e: Throwable => {
        404
      }
      case _ => 404
    }
  }

  def createLogIndex(index: String, shards: Int, replicas: Int): Future[Int] = {
    CreateFillableLogIndexQuery(esClient, index, shards, replicas).execute map {
      createResponse => if (createResponse.isAcknowledged) 200 else 404
    } recover {
      case e: IndexAlreadyExistsException => 400
      case _ => 404
    }
  }
}
