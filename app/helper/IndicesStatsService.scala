package helper

import scala.concurrent.Future
import esclient.queries.GetFillableIndicesQuery
import esclient.EsClient
import models.IndexListEntry
import play.api.libs.json.JsObject

class IndicesStatsService {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getIndexList: Future[List[IndexListEntry]] = {
    EsClient.execute(new GetFillableIndicesQuery) map {
      indices => {
        val indexListAsJson = (indices.json \ "indices").asInstanceOf[JsObject].fields filter {
          case (key, value) => key.startsWith("fbl_")
        }

        println(indexListAsJson.size)
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
}
