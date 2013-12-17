package helper.services

import scala.concurrent.Future
import esclient.queries._
import org.elasticsearch.search.facet.terms.TermsFacet
import scala.collection.JavaConversions._
import play.api.i18n.Messages
import models.{InputTopListEntry, Index}
import esclient.queries.CloseIndexQuery
import esclient.queries.GetTopInputValuesQuery
import esclient.Elasticsearch
import models.results.{EditSynonymsResult, ReindexResult, SynonymResult}

class SynonymService(es: Elasticsearch) {
  val esClient = es.client
  val termSplitRegex = List("=>", ",", "\\r?\\n").mkString("|").r
  val stringToListRegex = "\\r?\\n".r
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
    val indicesStatsService = new IndicesStatsService(es)

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
          case e: Throwable => SynonymResult(List.empty[InputTopListEntry], indexOption.getOrElse(Index("")).synonymEntries)
        }
      }
    } recover {
      case e: Throwable => SynonymResult(List.empty[InputTopListEntry], List.empty[String], Messages("error.cantGetSynonymgroups"))
    }
  }

  def editSynonyms(indexName: String, synonymGroupsString: String): Future[EditSynonymsResult] = {
    closeIndexForUpdate(indexName) flatMap {
      indexClosed => {
        if (indexClosed) {
          buildSynonymsAndUpdateIndex(indexName, synonymGroupsString) flatMap {
            synonymsBuilt => {
              if (synonymsBuilt) reopenIndexAndReindexChangedSynonyms(indexName, synonymGroupsString) map { reindexResult => reindexResult }
              else Future.successful(EditSynonymsResult(Messages("error.cantUpdateSynonyms", indexName)))
            }
          }
        } else Future.successful(EditSynonymsResult(Messages("error.cantCloseIndex", indexName)))
      }
    }
  }

  def closeIndexForUpdate(indexName: String): Future[Boolean] = {
    CloseIndexQuery(esClient, indexName).execute map {
      indexClosed => indexClosed.isAcknowledged
    } recover {
      case e: Throwable => false
    }
  }

  def buildSynonymsAndUpdateIndex(indexName: String, synonymGroupsString: String): Future[Boolean] = {
    convertSynonymGroupStringToList(indexName, synonymGroupsString) flatMap {
      list => EditFillableIndexSynonymsQuery(esClient, indexName, list).execute map {
        queryResult => queryResult.isAcknowledged
      } recover {
        case e: Throwable => false
      }
    }
  }

  def reopenIndexAndReindexChangedSynonyms(indexName: String, synonymGroupsString: String): Future[EditSynonymsResult] = {
    OpenIndexQuery(esClient, indexName).execute flatMap {
      indexOpened => {
        if (indexOpened.isAcknowledged) {
          reindexNewSynonyms(synonymGroupsString, indexName) map {
            reindexedSynonyms => EditSynonymsResult("", reindexedSynonyms)
          } recover {
            case e: Throwable => EditSynonymsResult("error.reindexFailed")
          }
        } else {
          Future.successful(EditSynonymsResult(Messages("error.cantOpenIndex", indexName)))
        }
      }
    } recover {
      case e: Throwable => EditSynonymsResult(Messages("error.cantOpenIndex", indexName))
    }
  }

  def reindexNewSynonyms(synonymText: String, indexname: String): Future[ReindexResult] = {
    val termArray = termSplitRegex.split(synonymText).map(entry => entry.trim)
    ReindexDocumentsBulkQuery(esClient, indexname, indexname, termArray).execute map {
      result => {
        ReindexResult(result.getItems.count(request => request.isFailed), result.getItems.length)
      }
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
    val synonymInput = stringToListRegex.split(synonymGroupString.toLowerCase).toList.map(entry => entry.trim())

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