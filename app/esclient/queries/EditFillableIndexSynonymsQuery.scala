package esclient.queries

import org.elasticsearch.client.Client
import scala.concurrent._
import org.elasticsearch.action.ActionListener
import play.api.libs.json.Json
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse

case class EditFillableIndexSynonymsQuery(esClient: Client, indexName: String, synonyms: List[String]) {
  lazy val p = promise[UpdateSettingsResponse]()

  esClient
    .admin()
    .indices()
    .prepareUpdateSettings()
    .setIndices(indexName)
    .setSettings(generateSettings())
    .execute()
    .addListener(new ActionListener[UpdateSettingsResponse] {

    def onFailure(e: Throwable) = p failure e

    def onResponse(response: UpdateSettingsResponse) = p success response
  })

  def execute: Future[UpdateSettingsResponse] = p.future

  def generateSettings() = {
    val settings = Json.obj(
      "analysis" -> Json.obj(
        "filter" -> Json.obj(
          indexName + "_filter" -> Json.obj(
            "type" -> "synonym",
            "synonyms" -> synonyms.toSeq
          )
        )
      )
    )

    Json.stringify(settings)
  }
}
