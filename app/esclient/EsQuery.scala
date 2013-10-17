package esclient

import play.api.libs.json._

abstract class EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
	def getResult(response: JsValue): JsObject 
}

object HttpType extends Enumeration {
     val Get = Value("get")
     val Post = Value("post")
     val Put = Value("put")
     val Delete = Value("delete")
}