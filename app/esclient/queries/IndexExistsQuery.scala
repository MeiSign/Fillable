package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class IndexExistsQuery(indexName: String) extends EsQuery {

  val httpType: HttpType.Value = HttpType.Head
  
  def getUrlAddon: String = "/" + indexName
  
  def toJson: JsObject = Json.obj()
}