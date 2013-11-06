package esclient.queries

import esclient.{HttpType, EsQuery}
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class GetDocumentById(indexName: String, option: String) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Get
  val documentId = option.hashCode

  def toJson: JsObject = Json.obj()

  def getUrlAddon: String = "/" + indexName + "/" + indexName + "/" + documentId
}
