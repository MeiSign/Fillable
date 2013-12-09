package helper.services

import scala.concurrent.Future
import models.{Index, IndexListEntry}
import org.elasticsearch.client.Client
import scala.collection.JavaConversions._
import esclient.queries.{GetFillableIndexQuery, GetFillableIndicesQuery}

class IndicesStatsService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getIndexList: Future[List[IndexListEntry]] = {
    GetFillableIndicesQuery(esClient).execute map {
      allIndices => {
        val indexList = allIndices.getIndices.toMap.filterKeys {
          case key => key.startsWith("fbl_") && !key.endsWith("_log")
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
    GetFillableIndexQuery(esClient).execute map {
      index => {
        val numberOfReplicas = index.getState.getMetaData.getIndices.get(indexName).getNumberOfReplicas
        val numberOfShards = index.getState.getMetaData.getIndices.get(indexName).getNumberOfShards
        val synonymEntries = index.getState.getMetaData.getIndices.get(indexName).getSettings.getAsArray("index.analysis.filter." + indexName + "_filter.synonyms").distinct
        Option(Index(indexName, numberOfShards, numberOfReplicas, logging = true, synonymEntries.toList))
      }
    } recover {
      case _ => None
    }
  }
}
