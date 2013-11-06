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

class DeleteFillableIndexQuery(name: String) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Delete
  
  def getUrlAddon: String = "/" + name
  
  def toJson: JsObject = Json.obj()
}

class CreateFillableIndexQuery(name: String, shards: Int = 4, replicas: Int = 0) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()

  val httpType: HttpType.Value = HttpType.Post
  
  def getUrlAddon: String = "/" + index
  
  def toJson: JsObject = Json.obj(
        "settings" -> Json.obj("number_of_shards" -> shards, "number_of_replicas" -> replicas),
        "mappings" -> Json.obj(
            index -> Json.obj(
                "properties" -> Json.obj(
                    "fillableOptions" -> Json.obj(
                        "type" -> "completion")
                        ))))
}