package helper.services

import scala.concurrent.Future
import esclient.queries.{GetFillableIndexQuery, GetFillableIndicesQuery}
import esclient.EsClient
import models.{Index, IndexListEntry}
import play.api.libs.json.JsObject
import org.elasticsearch.client.Client
import scala.collection.JavaConversions._

class IndicesStatsService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getIndexList: Future[List[IndexListEntry]] = {
    val getAllIndicesQuery = GetFillableIndicesQuery(esClient).execute

    getAllIndicesQuery map {
      allIndices => {
        val indexList = allIndices.getIndices.toMap.filterKeys {
          case key => key.startsWith("fbl_")
        }

        val result = for {
          (name, stats) <- indexList
        } yield {
          IndexListEntry(name, stats.getTotal.getDocs.getCount, stats.getTotal.getStore.getSize.getMb)
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
