package esclient

import play.api.libs.json._

trait EsQuery {
	val httpType: HttpType.Value
	def toJson: JsObject
	def getUrlAddon: String
}

object EsQuery extends EsQuery {
  val httpType: HttpType.Value = HttpType.get
  def toJson: JsObject = Json.obj()
  def getUrlAddon: String = ""
}

object HttpType extends Enumeration {
     val get = Value("get")
     val post = Value("post")
     val put = Value("put")
     val delete = Value("delete")
     val head = Value("head")
}

