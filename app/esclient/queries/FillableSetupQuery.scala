package esclient.queries

import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsObject


class FillableSetupQuery() extends EsQuery {

  val httpType: HttpType.Value = HttpType.Post
  
  def getUrlAddon: String = "/fbl_indices"
  
  def toJson: JsObject = Json.obj(
      "settings" -> Json.obj("number_of_shards" -> 1, "number_of_replicas" -> 0),
      "mappings" -> Json.obj(
            "indices" -> Json.obj(
                "properties" -> Json.obj(
                    "name" -> Json.obj(
                        "type" -> "String")
                        ))))
}