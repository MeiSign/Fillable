package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableIndexRegisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()
  val httpType: HttpType.Value = HttpType.Post
  
  def getUrlAddon: String = "/fbl_indices/indices"
  
  def toJson: JsObject = Json.obj("name" -> index, 
      "shards" -> shards, 
      "replicas" -> replicas)
}