package esclient.queries

import models.OptionDocument
import esclient.{HttpType, EsQuery}
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class AddOptionDocumentQuery(indexName: String, docIdString: String, doc: OptionDocument) extends EsQuery {
  val httpType: HttpType.Value = HttpType.Put
  val docId = docIdString.hashCode

  def toJson: JsObject = Json.obj(
    "fillableOptions" -> Json.obj(
      "input" -> doc.input,
      "output" -> doc.output,
      "weight" -> (doc.weight)
    )
  )

  def getUrlAddon: String = "/" + indexName + "/" + indexName + "/" + docId
}
