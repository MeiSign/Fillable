package esclient.queries

import esclient.{HttpType, EsQuery}
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class GetDocumentByIdQuery(indexName: String, id: String) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Get

  def toJson: JsObject = Json.obj()

  def getUrlAddon: String = "/" + indexName + "/" + indexName + "/" + id
}
