package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.ws.Response

class IndexExistsQuery(indexName: String) extends EsQuery {

  val httpType: HttpType.Value = HttpType.Head
  
  def getUrlAddon: String = "/" + indexName
  
  def toJson: JsObject = Json.obj()
  
  def getJsResult(response: Response): JsObject = Json.obj("httpCode" -> response.status)  
  
  def getBooleanResult(response: Response): Boolean = {
    val jsResponse = Json.obj("httpCode" -> response.status)
    (jsResponse \ "httpCode").as[Int] == 200
  }
}