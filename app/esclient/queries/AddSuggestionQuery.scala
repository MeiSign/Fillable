package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.ws.Response

class AddSuggestionQuery(index: String, suggestion: String) extends EsQuery {
  require(index != null, "indexName parameter must not be null")

  val httpType: HttpType.Value = HttpType.Put
  val indexName = index.toLowerCase

  def getUrlAddon(): String = "/" + indexName + "/" + indexName + "/" + suggestion.hashCode()

  def getJsResult(response: Response): JsObject = {
    require(response != null, "response JsValue must not be null")

    val jsResponse = response.json
    val error: Option[String] = (jsResponse \ "error").asOpt[String]

    if (!error.isDefined) EsQuery.respondWithSuccess(Json.obj())
    else EsQuery.respondWithError(error.get)
  }

  def toJson: JsObject = Json.obj(
    "name_suggest" -> suggestion,
    "weight" -> 5)
}