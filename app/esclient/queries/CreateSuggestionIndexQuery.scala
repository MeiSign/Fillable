package esclient.queries

import play.api.libs.json.JsObject
import esclient.EsQuery
import play.api.libs.json.JsValue
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.ws.Response


class CreateSuggestionIndexQuery(indexName: String) extends EsQuery {
  require(indexName != null, "indexName parameter must not be null")
  
  val httpType: HttpType.Value = HttpType.Put
  
  def getUrlAddon(): String = "/" + indexName

  def getJsResult(response: Response): JsObject = {
    require(response != null, "response JsValue must not be null")
    
    val jsResponse = response.json
    val error: Option[String] = (jsResponse \ "error").asOpt[String]
    
    if (!error.isDefined) {
      EsQuery.respondWithSuccess(Json.obj())
    } else {
      EsQuery.respondWithError(error.get)
    }
  }   
  
  def getBooleanResult(response: Response): Boolean = {
    require(response != null, "response JsValue must not be null")
    
    val jsResponse = response.json
    val error: Option[String] = (jsResponse \ "error").asOpt[String]
    !error.isDefined
  }
   
  def toJson: JsObject = 
    Json.obj(
        "mappings" -> Json.obj(
            indexName -> Json.obj(
                "properties" -> Json.obj(
                    "suggest" -> Json.obj(
                        "type" -> "completion")
                        ))))
}