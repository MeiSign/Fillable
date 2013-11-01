package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class EditFillableIndexQuery(name: String, replicas: Int) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Put
  
  def getUrlAddon: String = "/" + name + "/_settings"
  
  def toJson: JsObject = Json.obj("index" -> Json.obj("number_of_replicas" -> replicas))
}