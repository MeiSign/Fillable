package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json._
import play.Logger
import play.api.libs.ws.Response

class GetOptionsQuery(indexName: String, toBeCompleted: String) extends EsQuery {
  require(toBeCompleted != null, "toBeCompleted String must not be null")
  require(indexName != null, "indexName String must not be null")
  
  val httpType = HttpType.Post
  
  def getUrlAddon: String = "/" + indexName + "/_suggest"
  
  def toJson: JsObject = 
     Json.obj(
    		 indexName -> Json.obj(
    		     "text" -> toBeCompleted,
    		     "completion" -> Json.obj("field" -> "fillableSuggest", "fuzzy" -> Json.obj("edit_distance" -> 1))
    		 )
     )
}