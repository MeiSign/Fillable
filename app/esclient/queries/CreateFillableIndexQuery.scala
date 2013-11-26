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

  val mapping: XContentBuilder = jsonBuilder()
    .startObject()
    .startObject("fbl_" + name)
    .startObject("properties")
    .startObject("fillableOptions")
    .field("type", "completion")
    .endObject()
    .endObject()
    .endObject()
    .endObject()

  val settings = ImmutableSettings.settingsBuilder()
    .put("number_of_shards", shards)
    .put("number_of_replicas", replicas)
    .build()

  esClient
    .admin()
    .indices()
    .prepareCreate("fbl_" + name)
    .addMapping("fbl_" + name, mapping)
    .setSettings(settings)
    .execute()
    .addListener(new ActionListener[CreateIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: CreateIndexResponse) = p success response
  })

  def execute: Future[CreateIndexResponse] = p.future
}