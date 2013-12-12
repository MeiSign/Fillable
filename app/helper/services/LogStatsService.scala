package helper.services

import scala.concurrent.Future
import models.{LogListResult, LogListEntry}
import scala.collection.JavaConversions._
import esclient.queries.GetFillableIndicesQuery
import esclient.Elasticsearch
import org.elasticsearch.action.admin.indices.stats.IndexStats

class LogStatsService(es: Elasticsearch) {
  val esClient = es.client
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getLogLists: Future[LogListResult] = {
    GetFillableIndicesQuery(esClient).execute map {
      allIndices => {
        val allIndicesList = allIndices.getIndices.toMap
        val logMap = allIndicesList.filterKeys { case name => indexIsLogIndex(name) }
        val deactivatedLogsMap = allIndicesList.filterKeys { case name => indexHasNoLogIndex(name, allIndicesList) }

        val activatedLogs = for {
          (name, stats) <- logMap
        } yield {
          LogListEntry(name, stats.getTotal.getDocs.getCount, stats.getTotal.getStore.getSize.getMb)
        }

        val deactivatedLogs = for {
          (name, stats) <- deactivatedLogsMap
        } yield {
          LogListEntry(name + "_log", 0, 0)
        }

        LogListResult(activatedLogs.toList, deactivatedLogs.toList)
      }
    } recover {
      case _ => LogListResult(List.empty[LogListEntry], List.empty[LogListEntry])
    }
  }

  def indexIsLogIndex(name: String): Boolean = name.startsWith("fbl_") && name.endsWith("_log")
  def indexHasNoLogIndex(name: String, allIndicesList: Map[String, IndexStats]): Boolean = name.startsWith("fbl_") && !name.endsWith("_log") && !allIndicesList.contains(name + "_log")

}
