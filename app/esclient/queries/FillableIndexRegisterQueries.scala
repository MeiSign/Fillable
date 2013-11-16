package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableIndexRegisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()
  val httpType: HttpType.Value = HttpType.post
  
  def getUrlAddon: String = "/fbl_indices/indices/" + index.hashCode()
  
  def toJson: JsObject = Json.obj("name" -> index, 
      "shards" -> shards, 
      "replicas" -> replicas)
}

class FillableIndexReregisterQuery(name: String, shards: Int, replicas: Int) extends EsQuery {
  val httpType: HttpType.Value = HttpType.put
  
  def getUrlAddon: String = "/fbl_indices/indices/" + name.hashCode()
  
  def toJson: JsObject = Json.obj("name" -> name, 
      "shards" -> shards, 
      "replicas" -> replicas)
}

class FillableIndexUnregisterQuery(name: String) extends EsQuery {
  val httpType: HttpType.Value = HttpType.delete
  
  def getUrlAddon: String = "/fbl_indices/indices/" + name.hashCode()
  
  def toJson: JsObject = Json.obj()
}

class FillableIndexSetupQuery() extends EsQuery {
  val httpType: HttpType.Value = HttpType.post

  def getUrlAddon: String = "/fbl_indices"

  def toJson: JsObject = Json.obj(
    "settings" -> Json.obj("number_of_shards" -> 1, "number_of_replicas" -> 0),
    "mappings" -> Json.obj(
      "indices" -> Json.obj(
        "properties" -> Json.obj(
          "name" -> Json.obj("type" -> "String"),
          "shards" -> Json.obj("type" -> "integer"),
          "replicas" -> Json.obj("type" -> "integer")
        ))))
}