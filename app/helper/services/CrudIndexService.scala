package helper.services

import scala.concurrent.Future
import org.elasticsearch.indices.{IndexAlreadyExistsException, IndexMissingException}
import esclient.queries.{EditFillableIndexQuery, DeleteFillableIndexQuery, CreateFillableIndexQuery}
import esclient.Elasticsearch

class CrudIndexService(es: Elasticsearch) {
  val esClient = es.client
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def editFillableIndex(index: String, replicas: Int): Future[Int] = {
    EditFillableIndexQuery(esClient, index, replicas).execute map {
      editResponse => if (editResponse.isAcknowledged) 200 else 400
    } recover {
      case e: IndexMissingException => 404
      case _ => 400
    }
  }

  def deleteEsIndex(index: String): Future[Int] = {
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

  def deleteFillableIndex(index: String): Future[Int] = {
    for {
      indexDeleted: Int <- deleteEsIndex(index)
      logIndexDeleted: Int <- deleteEsIndex(index + "_log")
    } yield {
      (indexDeleted, logIndexDeleted) match {
        case (200, 200) => 200
        case (200, 404) => 200
        case (404, 404) => 200
        case _ => 400
      }
    }
  }


  def createFillableIndex(index: String, shards: Int, replicas: Int, logging: Boolean): Future[Int] = {
    val indexName =  "fbl_" + index
    val logIndexService = new LogIndexService(es)
    createEsIndex(indexName, shards, replicas) flatMap {
      indexCreated => {
        logging match  {
          case true => logIndexService.createLogIndex(indexName + "_log", shards, replicas) map {
              logCreated => (logCreated, indexCreated) match {
                case (200, 200) => 200
                case _ => 404
              }
          }
          case false => Future.successful(indexCreated)
        }
      }
    }
  }

  def createEsIndex(index: String, shards: Int, replicas: Int): Future[Int] = {
    CreateFillableIndexQuery(esClient, index, shards, replicas).execute map {
      createResponse => if (createResponse.isAcknowledged) 200 else 404
    } recover {
      case e: IndexAlreadyExistsException => 400
      case e: Throwable => {
        e.printStackTrace()
        404
      }
      case _ => 404
    }
  }
}
