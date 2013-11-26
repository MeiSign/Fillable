package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import org.elasticsearch.common.settings.{ImmutableSettings}
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse

case class EditFillableIndexQuery(esClient: Client, name: String, replicas: Int) {
  lazy val p = promise[UpdateSettingsResponse]()

  esClient
    .admin()
    .indices()
    .prepareUpdateSettings()
    .setIndices(name)
    .setSettings(ImmutableSettings.settingsBuilder().put("number_of_replicas", replicas).build())
    .execute()
    .addListener(new ActionListener[UpdateSettingsResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: UpdateSettingsResponse) = p success response
  })

  def execute: Future[UpdateSettingsResponse] = p.future
}

case class DeleteFillableIndexQuery(esClient: Client, name: String) {
  lazy val p = promise[DeleteIndexResponse]()

  esClient
    .admin()
    .indices()
    .prepareDelete(name)
    .execute()
    .addListener(new ActionListener[DeleteIndexResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: DeleteIndexResponse) = p success response
  })

  def execute: Future[DeleteIndexResponse] = p.future
}

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