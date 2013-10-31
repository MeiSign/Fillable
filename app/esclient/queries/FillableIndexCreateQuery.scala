package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableIndexCreateQuery(name: String, shards: Int = 4, replicas: Int = 0) extends EsQuery {
  val index = "fbl_" + name.toLowerCase()

  val httpType: HttpType.Value = HttpType.Post
  
  def getUrlAddon: String = "/" + index
  
  def toJson: JsObject = Json.obj(
        "settings" -> Json.obj("number_of_shards" -> shards, "number_of_replicas" -> replicas),
        "mappings" -> Json.obj(
            index -> Json.obj(
                "properties" -> Json.obj(
                    "fillableSuggest" -> Json.obj(
                        "type" -> "completion")
                        ))))
}