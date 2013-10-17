object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json

val response: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0}}""")
                                                  //> response  : play.api.libs.json.JsValue = {"_shards":{"total":5,"successful":
                                                  //| 5,"failed":0}}
val string: Option[String] = (response \ "error").asOpt[String]
                                                  //> string  : Option[String] = None
string.isDefined                                  //> res0: Boolean = false
string.isEmpty                                    //> res1: Boolean = true
string.nonEmpty                                   //> res2: Boolean = false
}