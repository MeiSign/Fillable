package esclient

import play.api.libs.json._
import play.api.libs.ws.Response

trait EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
	def getJsResult(response: Response): JsObject 
}

object EsQuery extends EsQuery {
  val httpType: HttpType.Value = HttpType.Get
  def toJson: JsObject = Json.obj()
  def getUrlAddon: String = ""
  def getJsResult(response: Response): JsObject = Json.obj() 
  def respondWithError(msg: String): JsObject = Json.obj("status" -> "error", "msg" -> msg)
  def respondWithSuccess(obj: JsObject): JsObject = Json.obj("status" -> "ok") ++ obj
}

object HttpType extends Enumeration {
     val Get = Value("get")
     val Post = Value("post")
     val Put = Value("put")
     val Delete = Value("delete")
     val Head = Value("head")
}

