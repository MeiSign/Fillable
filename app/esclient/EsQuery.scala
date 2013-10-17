package esclient

import play.api.libs.json._

abstract class EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
	def getResult(response: JsValue): JsObject 
  def respondError(msg: String): JsObject = Json.obj("status" -> "error", "msg" -> msg)
  def respondSucces(obj: JsObject): JsObject = Json.obj("status" -> "ok") ++ obj
}

object HttpType extends Enumeration {
     val Get = Value("get")
     val Post = Value("post")
     val Put = Value("put")
     val Delete = Value("delete")
}

