package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class IndexExistsQuery(indexName: String, typeName: String) extends EsQuery {

  val httpType: HttpType.Value = HttpType.head
  
  def getUrlAddon: String = "/" + indexName + "/" + typeName
  
  def toJson: JsObject = Json.obj()
}