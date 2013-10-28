package esclient

import play.api.libs.json._
import play.api.libs.ws.Response
import scala.concurrent.Future

trait EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
}

object EsQuery extends EsQuery {
  val httpType: HttpType.Value = HttpType.Get
  def toJson: JsObject = Json.obj()
  def getUrlAddon: String = ""
}

object HttpType extends Enumeration {
     val Get = Value("get")
     val Post = Value("post")
     val Put = Value("put")
     val Delete = Value("delete")
     val Head = Value("head")
}

