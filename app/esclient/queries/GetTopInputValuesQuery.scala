package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.facet.FacetBuilders
import org.elasticsearch.action.search.SearchResponse

case class GetTopInputValuesQuery(esClient: Client, indexName: String, exceptions: List[String]) {
  lazy val p = promise[SearchResponse]()
  esClient
    .prepareSearch(indexName)
    .setQuery(QueryBuilders.matchAllQuery())
    .addFacet(FacetBuilders.termsFacet("topTen").field("chosen").size(10).exclude(exceptions.toArray))
    .execute()
    .addListener(new ActionListener[SearchResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: SearchResponse) = p success response
  })

  def execute: Future[SearchResponse] = p.future
}
