package esclient.queries

import play.api.libs.json._

abstract class EsQuery {
	def toJson: JsObject
	def getUrlAddon : String
}

class FindCompletionsQuery(toBeCompleted: String, fieldName: String, indexName: String) extends EsQuery {
  require(toBeCompleted != null, "toBeCompleted String must not be null")
  require(fieldName != null, "fieldName String must not be null")
  require(indexName != null, "indexName String must not be null")
  
  def getUrlAddon: String = "/" + indexName + "/_suggest"
  
  def toJson: JsObject = {
     Json.obj(
    		 indexName -> Json.obj(
    		     "text" -> toBeCompleted,
    		     "completion" -> Json.obj("field" -> fieldName)
    		 )
     )
   }
}