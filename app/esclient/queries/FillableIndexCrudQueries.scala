package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import org.elasticsearch.client.Client
import scala.concurrent._
import play.api.libs.json.JsObject
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.ActionListener
import play.api.libs.json.JsObject
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsResponse

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

class DeleteFillableIndexQuery(name: String) extends EsQuery {
  val httpType: HttpType.Value = HttpType.delete
  
  def getUrlAddon: String = "/" + name
  
  def toJson: JsObject = Json.obj()
}

class CreateFillableIndexQuery(name: String, shards: Int = 4, replicas: Int = 0) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()

  val httpType: HttpType.Value = HttpType.post
  
  def getUrlAddon: String = "/" + index
  
  def toJson: JsObject = Json.obj(
        "settings" -> Json.obj("number_of_shards" -> shards, "number_of_replicas" -> replicas),
        "mappings" -> Json.obj(
            index -> Json.obj(
                "properties" -> Json.obj(
                    "fillableOptions" -> Json.obj(
                        "type" -> "completion")
                        ))))
}