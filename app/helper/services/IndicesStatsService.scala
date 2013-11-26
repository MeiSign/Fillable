package helper.services

import scala.concurrent.Future
import models.{Index, IndexListEntry}
import org.elasticsearch.client.Client
import scala.collection.JavaConversions._
import esclient.queries.{GetFillableIndicesQuery, GetFillableIndexQuery}

class IndicesStatsService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getIndexList: Future[List[IndexListEntry]] = {
    GetFillableIndicesQuery(esClient).execute map {
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
    GetFillableIndexQuery(esClient, indexName).execute map {
      index => {
        val numberOfReplicas = index.getState.getMetaData.getIndices.get(indexName).getNumberOfReplicas
        val numberOfShards = index.getState.getMetaData.getIndices.get(indexName).getNumberOfShards
        Option(Index(indexName, numberOfShards, numberOfReplicas))
      }
    } recover {
      case _ => None
    }
  }
}
