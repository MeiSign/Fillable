package helper

import scala.concurrent.Future
import esclient.queries.{GetFillableIndexQuery, GetFillableIndicesQuery}
import esclient.EsClient
import models.{Index, IndexListEntry}
import play.api.libs.json.JsObject

class IndicesStatsService {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getIndexList: Future[List[IndexListEntry]] = {
    EsClient.execute(new GetFillableIndicesQuery) map {
      indices => {
        val indexListAsJson = (indices.json \ "indices").asInstanceOf[JsObject].fields filter {
          case (key, value) => key.startsWith("fbl_")
        }

        val result = for {
          (name, stats) <- indexListAsJson
        } yield {
          IndexListEntry(name, (stats \ "total" \ "docs" \ "count").as[Int], (stats \ "total" \ "store" \ "size").as[String])
        }

        result.toList
      }
    } recover {
      case _ => List.empty[IndexListEntry]
    }
  }

  def getIndexSettings(indexName: String): Future[Option[Index]] = {
    val query = new GetFillableIndexQuery(indexName)
    EsClient.execute(query) map {
      index => {
        if (index.status == 200) {
          val shards = (index.json \\ "index.number_of_shards").head.as[String].toInt
          val replica = (index.json \\ "index.number_of_replicas").head.as[String].toInt
          val indexModel = Index(indexName, shards, replica)
          Option(indexModel)
        } else {
          None
        }
      }
    } recover {
      case _ =>  None
    }
  }
}
