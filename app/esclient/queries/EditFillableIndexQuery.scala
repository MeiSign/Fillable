package esclient.queries

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.action.ActionListener
import scala.concurrent._
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse

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
