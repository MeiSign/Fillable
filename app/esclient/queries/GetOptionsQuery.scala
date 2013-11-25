package esclient.queries

import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder
import org.elasticsearch.action.suggest.SuggestResponse

case class GetOptionsQuery(esClient: Client, indexName: String, toBeCompleted: String) {
  lazy val p = promise[SuggestResponse]()
  val query = esClient
    .prepareSuggest(indexName)
    .addSuggestion(new CompletionSuggestionFuzzyBuilder(indexName).field("fillableOptions").text(toBeCompleted).size(10))
    .execute()
    .addListener(new ActionListener[SuggestResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: SuggestResponse) = p success response
  })

  def execute: Future[SuggestResponse] = p.future
}