package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries._
import org.elasticsearch.search.facet.terms.TermsFacet
import scala.collection.JavaConversions._
import play.api.i18n.Messages
import models.Index
import esclient.queries.CloseIndexQuery
import esclient.queries.GetTopInputValuesQuery

class SynonymService(esClient: Client) {
  val termSplitRegex = List("=>", ",").mkString("|").r
  val stringToListRegex = "\r\n".r
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global


  def getSynonymsAndTopInputValues(indexName: String) : Future[SynonymResult] = {
    val indicesStatsService = new IndicesStatsService(esClient)

    indicesStatsService.getIndexSettings(indexName) flatMap {
      indexOption => {
        val synonyms: List[String] = indexOption.getOrElse(Index("")).synonymEntries flatMap {
          entry => termSplitRegex.split(entry)
        }
        GetTopInputValuesQuery(esClient, indexName + "_log", synonyms).execute map {
          facetResponse => {
            val f: TermsFacet = facetResponse.getFacets.facetsAsMap.get("topTen").asInstanceOf[TermsFacet]
            val topTen: List[String] = f.getEntries.iterator.toList map { entry => entry.getTerm.string }

            SynonymResult(topTen, indexOption.getOrElse(Index("")).synonymEntries)
          }
        } recover {
          case e: Throwable => SynonymResult(List.empty[String], List.empty[String], Messages("error.cantGetTopTenInputValues"))
        }
      }
    } recover {
      case e: Throwable => SynonymResult(List.empty[String], List.empty[String], Messages("error.cantGetSynonymgroups"))
    }
  }

  def editSynonyms(indexName: String, synonymGroupString: String) : Future[Int] = {
    val synonymGroups: List[String] = convertSynonymGroupStringToList(synonymGroupString)
    CloseIndexQuery(esClient, indexName).execute flatMap {
      closeIndexResponse => EditFillableIndexSynonymsQuery(esClient, indexName, synonymGroups).execute flatMap {
        editFillableIndexSynonymsResponse => OpenIndexQuery(esClient, indexName).execute map {
          openIndexResponse => if (openIndexResponse.isAcknowledged) 200 else 403
        } recover {
          case e: Throwable => 401
        }
      } recover {
        case e: Throwable => 402
      }
    } recover {
      case e: Throwable => 400
    }
  }

  def convertSynonymGroupStringToList(synonymGroupString: String): List[String] = {
    stringToListRegex.split(synonymGroupString).toList
  }
}

case class SynonymResult(topTenInputValues: List[String] = List.empty[String],
                         synonymGroups: List[String] = List.empty[String],
                         error: String = "") {
  def hasError : Boolean = {
    return !error.isEmpty
  }
}
