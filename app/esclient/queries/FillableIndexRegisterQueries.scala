package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableIndexRegisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()
  val httpType: HttpType.Value = HttpType.Post
  
  def getUrlAddon: String = "/fbl_indices/indices/" + index.hashCode()
  
  def toJson: JsObject = Json.obj("name" -> index, 
      "shards" -> shards, 
      "replicas" -> replicas)
}

class FillableIndexReregisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Put
  
  def getUrlAddon: String = "/fbl_indices/indices/" + name.hashCode()
  
  def toJson: JsObject = Json.obj("name" -> name, 
      "shards" -> shards, 
      "replicas" -> replicas)
}

class FillableIndexUnregisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Delete
  
  def getUrlAddon: String = "/fbl_indices/indices/" + name.hashCode()
  
  def toJson: JsObject = Json.obj()
}