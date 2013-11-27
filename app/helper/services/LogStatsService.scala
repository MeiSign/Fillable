package helper.services

import scala.concurrent.Future
import models.LogListEntry
import org.elasticsearch.client.Client
import scala.collection.JavaConversions._
import esclient.queries.GetFillableIndicesQuery

class LogStatsService(esClient: Client) {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getLogLists: Future[Map[String, List[LogListEntry]]] = {
    GetFillableIndicesQuery(esClient).execute map {
      allIndices => {
        val logMap = allIndices.getIndices.toMap.filterKeys { case key => key.startsWith("fbl_") && key.endsWith("_log") }
        val deactivatedLogsMap = allIndices.getIndices.toMap.filterKeys { case key => !key.endsWith("_log") && !allIndices.getIndices.toMap.contains(key + "_log") }

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

        Map("activated" -> activatedLogs.toList, "deactivated" -> deactivatedLogs.toList)
      }
    } recover {
      case _ => Map("activated" -> List.empty[LogListEntry], "deactivated" -> List.empty[LogListEntry])
    }
  }
}
