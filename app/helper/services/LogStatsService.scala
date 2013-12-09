package helper.services

import scala.concurrent.Future
import models.{LogListResult, LogListEntry}
import org.elasticsearch.client.Client
import scala.collection.JavaConversions._
import esclient.queries.GetFillableIndicesQuery

class LogStatsService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getLogLists: Future[LogListResult] = {
    GetFillableIndicesQuery(esClient).execute map {
      allIndices => {
        val logMap = allIndices.getIndices.toMap.filterKeys { case key => key.startsWith("fbl_") && key.endsWith("_log") }
        val deactivatedLogsMap = allIndices.getIndices.toMap.filterKeys { case key => key.startsWith("fbl_") && !key.endsWith("_log") && !allIndices.getIndices.toMap.contains(key + "_log") }

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
}
