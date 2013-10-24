package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json._
import play.Logger
import play.api.libs.ws.Response

class GetSuggestionsQuery(indexName: String, toBeCompleted: String) extends EsQuery {
  require(toBeCompleted != null, "toBeCompleted String must not be null")
  require(indexName != null, "indexName String must not be null")
  
  val httpType = HttpType.Post
  
  def getUrlAddon: String = "/" + indexName + "/_suggest"
  
  def toJson: JsObject = 
     Json.obj(
    		 indexName -> Json.obj(
    		     "text" -> toBeCompleted,
    		     "completion" -> Json.obj("field" -> "suggest", "fuzzy" -> Json.obj("edit_distance" -> 1))
    		 )
     )
  
  def getJsResult(response: Response): JsObject = {
    require(response != null, "response JsValue must not be null")
    
    val jsResponse = response.json
    val error: Option[String] = (jsResponse \ "error").asOpt[String]
    
    if (!error.isDefined) {
      val options: Seq[JsValue] = (jsResponse \\ "options")
      if (options.size > 0) EsQuery.respondWithSuccess(Json.obj("completions" -> options(0))) 
      else EsQuery.respondWithSuccess(Json.obj("completions" -> List[String]()))
    } else {
      EsQuery.respondWithError(error.get)
    }
  }
}