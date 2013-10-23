package esclient.queries

import play.api.libs.json.JsObject
import esclient.EsQuery
import play.api.libs.json.JsValue
import esclient.HttpType
import play.api.libs.json.Json


class CreateCompletionsFieldQuery(indexName: String) extends EsQuery {
  require(indexName != null, "indexName parameter must not be null")
  
  val httpType: HttpType.Value = HttpType.Put
  
  def getUrlAddon(): String = "/" + indexName

  def getResult(response: JsValue): JsObject = {
    require(response != null, "response JsValue must not be null")
    
    val error: Option[String] = (response \ "error").asOpt[String]
    
    if (!error.isDefined) {
      respondSuccess(Json.obj())
    } else {
      respondError(error.get)
    }
  }   
   
  def toJson: JsObject = 
    Json.obj(
        "mappings" -> Json.obj(
            indexName -> Json.obj(
                "properties" -> Json.obj(
                    "suggest" -> Json.obj(
                        "type" -> "completion"),
                    "searchId" -> Json.obj(
                        "type" -> "string")))))
}