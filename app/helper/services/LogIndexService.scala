package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import org.elasticsearch.indices.IndexAlreadyExistsException
import org.elasticsearch.common.xcontent.XContentFactory._
import esclient.queries.{GetFillableIndexQuery, CreateFillableLogIndexQuery, DeleteFillableIndexQuery, IndexDocumentQuery}

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

  def addLogEntry(indexName: String, typed: String, chosen: String): Future[Int] = {
    GetFillableIndexQuery(esClient).execute flatMap {
      index => {
        if (index.getState.getMetaData.getIndices.containsKey(indexName)) {
          val doc = jsonBuilder()
            .startObject()
            .field("timestamp", System.currentTimeMillis())
            .field("typed", typed)
            .field("chosen", chosen)
            .endObject().string()

          IndexDocumentQuery(esClient, indexName, doc).execute map {
            indexResponse => 200
          } recover {
            case _ => 400
          }
        } else {
          Future.successful(202)
        }
      }
    } recover {
      case _ => 400
    }
  }
}
