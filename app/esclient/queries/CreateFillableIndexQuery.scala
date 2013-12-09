package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse

case class CreateFillableIndexQuery(esClient: Client, name: String, shards: Int = 4, replicas: Int = 0) {
  lazy val p = promise[CreateIndexResponse]()

  val mapping = generateMapping
  val settings = generateSettings

  esClient
    .admin()
    .indices()
    .prepareCreate(name)
    .addMapping(name, mapping)
    .setSettings(settings)
    .execute()
    .addListener(new ActionListener[CreateIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: CreateIndexResponse) = p success response
  })

  def execute: Future[CreateIndexResponse] = p.future

  def generateMapping: XContentBuilder = {
    jsonBuilder()
      .startObject()
      .startObject(name)
      .startObject("properties")
      .startObject("fillableOptions")
      .field("type", "completion")
      .field("index_analyzer", name + "_analyzer")
      .field("search_analyzer", name + "_analyzer")
      .endObject()
      .endObject()
      .endObject()
      .endObject()
  }

  def generateSettings: ImmutableSettings.Builder = {
    ImmutableSettings.settingsBuilder()
      .put("number_of_shards", shards)
      .put("number_of_replicas", replicas)
      .loadFromSource(jsonBuilder()
      .startObject()
      .startObject("analysis")
      .startObject("analyzer")
      .startObject(name + "_analyzer")
      .field("type", "custom")
      .field("tokenizer", "lowercase")
      .field("filter", name + "_filter")
      .endObject()
      .endObject()
      .startObject("filter")
      .startObject(name + "_filter")
      .field("type", "synonym")
      .startArray("synonyms")
      .value("-, _")
      .endArray()
      .endObject()
      .endObject()
      .endObject()
      .endObject
      .string()
    )
  }
}
