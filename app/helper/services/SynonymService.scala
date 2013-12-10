package helper.services

import org.elasticsearch.client.Client
import scala.concurrent.Future
import esclient.queries._
import org.elasticsearch.search.facet.terms.TermsFacet
import scala.collection.JavaConversions._
import play.api.i18n.Messages
import models.{InputTopListEntry, Index}
import esclient.queries.CloseIndexQuery
import esclient.queries.GetTopInputValuesQuery

class SynonymService(esClient: Client) {
  val termSplitRegex = List("=>", ",").mkString("|").r
  val stringToListRegex = "\r\n".r
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def getTopInputValues(indexName:String): Future[List[InputTopListEntry]] = {
    GetTopInputValuesQuery(esClient, indexName + "_log", List.empty[String]).execute map {
      facetResponse => {
        val f: TermsFacet = facetResponse.getFacets.facetsAsMap.get("topTen").asInstanceOf[TermsFacet]
        f.getEntries.iterator.toList map { entry => InputTopListEntry(entry.getTerm.string, entry.getCount) }
      }
    } recover {
      case e: Throwable => List.empty[InputTopListEntry]
    }
  }

  def getSynonymsAndTopInputValues(indexName: String) : Future[SynonymResult] = {
    val indicesStatsService = new IndicesStatsService(esClient)

    indicesStatsService.getIndexSettings(indexName) flatMap {
      indexOption => {
        val synonyms: List[String] = indexOption.getOrElse(Index("")).synonymEntries flatMap {
          entry => termSplitRegex.split(entry).map(str => str.trim)
        }
        GetTopInputValuesQuery(esClient, indexName + "_log", synonyms).execute map {
          facetResponse => {
            val f: TermsFacet = facetResponse.getFacets.facetsAsMap.get("topTen").asInstanceOf[TermsFacet]
            val topTen: List[InputTopListEntry] = f.getEntries.iterator.toList map { entry => InputTopListEntry(entry.getTerm.string, entry.getCount) }

            SynonymResult(topTen, indexOption.getOrElse(Index("")).synonymEntries)
          }
        } recover {
          case e: Throwable => SynonymResult(List.empty[InputTopListEntry], List.empty[String], Messages("error.cantGetTopTenInputValues"))
        }
      }
    } recover {
      case e: Throwable => SynonymResult(List.empty[InputTopListEntry], List.empty[String], Messages("error.cantGetSynonymgroups"))
    }
  }

  def editSynonyms(indexName: String, synonymGroupString: String) : Future[Int] = {
    convertSynonymGroupStringToList(indexName, synonymGroupString) flatMap {
      synonymGroups => CloseIndexQuery(esClient, indexName).execute flatMap {
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
    } recover {
      case e: Throwable => 403
    }
  }

  def getCurrentSynonymFilterSize(indexName: String): Future[Int] = {
    getSynonymsAndTopInputValues(indexName) map {
      result => result.synonymGroups.length
    } recover {
      case e: Throwable => 0
    }
  }

  def convertSynonymGroupStringToList(indexName: String, synonymGroupString: String): Future[List[String]] = {
    val synonymInput = stringToListRegex.split(synonymGroupString.toLowerCase).toList

    getCurrentSynonymFilterSize(indexName) map {
      filterSize => {
        if (synonymInput.length >= filterSize) synonymInput
        else synonymInput ::: List.fill(filterSize - synonymInput.length)(" ")
      }
    } recover {
      case e: Throwable => List.empty[String]
    }
  }
}

case class SynonymResult(topTenInputValues: List[InputTopListEntry] = List.empty[InputTopListEntry],
                         synonymGroups: List[String] = List.empty[String],
                         error: String = "") {

  def hasError : Boolean = !error.isEmpty
}