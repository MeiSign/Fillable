package esclient.queries

import play.api.libs.json.JsObject
import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import play.api.i18n.Messages
import play.api.libs.ws.Response

class GetEsVersionQuery extends EsQuery {
 val httpType = HttpType.get
  
  def getUrlAddon: String = "/"
  
  def toJson: JsObject = Json.obj()
}