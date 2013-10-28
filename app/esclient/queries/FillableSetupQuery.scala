package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableSetupQuery() extends EsQuery {

  val httpType: HttpType.Value = HttpType.Put
  
  def getUrlAddon: String = "/fbl_indices/indices"
  
  def toJson: JsObject = Json.obj()
}