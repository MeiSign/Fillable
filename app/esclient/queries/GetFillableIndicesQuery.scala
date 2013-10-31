package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class GetFillableIndicesQuery extends EsQuery {
  
  val httpType: HttpType.Value = HttpType.Get

  def getUrlAddon: String = "/fbl_indices/indices/_search"

  def toJson: JsObject = Json.obj()
}