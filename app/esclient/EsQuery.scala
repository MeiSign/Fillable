package esclient

import play.api.libs.json._

abstract class EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
	def getResult(response: JsValue): JsObject 
	val ErrorResponse: JsObject = Json.obj("status" -> "error");
	val SuccessResponse: JsObject = Json.obj("status" -> "ok");
}

object HttpType extends Enumeration {
     val Get = Value("get")
     val Post = Value("post")
     val Put = Value("put")
     val Delete = Value("delete")
}