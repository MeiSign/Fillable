package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries.{CloseIndexQuery, OpenIndexQuery, CreateFillableLogIndexQuery}
import org.elasticsearch.indices.IndexAlreadyExistsException

class LogIndexService(esClient: Client) {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def createLogIndex(index: String, shards: Int, replicas: Int, logging: Boolean): Future[Int] = {
    createLogEsIndex(index, shards, replicas) flatMap {
      case 200 if logging => Future.successful(200)
      case 200 if !logging => closeLogIndex(index)
      case _ => Future.successful(400)
    } recover {
      case e: IndexAlreadyExistsException => 400
      case _ => 404
    }
  }

  def openLogIndex(index: String): Future[Int] = {
    OpenIndexQuery(esClient, index).execute map {
      openResponse => if (openResponse.isAcknowledged) 200 else 404
    } recover {
      case _ => 404
    }
  }

  def closeLogIndex(index: String): Future[Int] = {
    CloseIndexQuery(esClient, index).execute map {
      closeResponse => if (closeResponse.isAcknowledged) 200 else 404
    } recover {
      case _ => 404
    }
  }

  def createLogEsIndex(index: String, shards: Int, replicas: Int): Future[Int] = {
    CreateFillableLogIndexQuery(esClient, index, shards, replicas).execute map {
      createResponse => if (createResponse.isAcknowledged) 200 else 404
    } recover {
      case e: IndexAlreadyExistsException => 400
      case _ => 404
    }
  }
}
