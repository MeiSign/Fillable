package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class GetFillableIndicesQuery extends EsQuery {
  
  val httpType: HttpType.Value = HttpType.get

  def getUrlAddon: String = "/_stats"

  def toJson: JsObject = Json.obj()
}

class GetFillableIndexQuery(index: String) extends EsQuery {
  
  val httpType: HttpType.Value = HttpType.get

  def getUrlAddon: String = "/fbl_indices/indices/" + index.hashCode + "/_source"

  def toJson: JsObject = Json.obj()
}