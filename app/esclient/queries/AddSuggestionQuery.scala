package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.ws.Response

class AddSuggestionQuery(indexName: String, suggestion: String) extends EsQuery {
  require(indexName != null, "indexName parameter must not be null")

  val httpType: HttpType.Value = HttpType.Put

  def getUrlAddon(): String = "/" + indexName + "/" + indexName + "/" + suggestion.hashCode()

  def getJsResult(response: Response): JsObject = {
    Json.obj("funzt" -> "super")

    //    require(response != null, "response JsValue must not be null")
    //    
    //    val error: Option[String] = (response \ "error").asOpt[String]
    //    
    //    if (!error.isDefined) {
    //      EsQuery.respondWithSuccess(Json.obj())
    //    } else {
    //      EsQuery.respondWithError(error.get)
    //    }
  }

  def getBooleanResult(response: JsValue): Boolean = {
    require(response != null, "response JsValue must not be null")

    val error: Option[String] = (response \ "error").asOpt[String]
    !error.isDefined
  }

  def toJson: JsObject = Json.obj(
    "name_suggest" -> suggestion,
    "weight" -> 5)
}