package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json._

class FindCompletionsQuery(indexName: String, fieldName: String, toBeCompleted: String) extends EsQuery {
  require(toBeCompleted != null, "toBeCompleted String must not be null")
  require(fieldName != null, "fieldName String must not be null")
  require(indexName != null, "indexName String must not be null")
  
  val httpType = HttpType.Post
  
  def getUrlAddon: String = "/" + indexName + "/_suggest"
  
  def toJson: JsObject = {
     Json.obj(
    		 indexName -> Json.obj(
    		     "text" -> toBeCompleted,
    		     "completion" -> Json.obj("field" -> fieldName)
    		 )
     )
   }
  
  def getResult(response: JsValue): JsObject = {
    val options: Seq[JsValue] = (response \\ "options")
    Json.obj("completions" -> options(0))
  }
}