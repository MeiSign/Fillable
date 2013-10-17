object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(176); 

val response: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0}}""");System.out.println("""response  : play.api.libs.json.JsValue = """ + $show(response ));$skip(64); 
val string: Option[String] = (response \ "error").asOpt[String];System.out.println("""string  : Option[String] = """ + $show(string ));$skip(17); val res$0 = 
string.isDefined;System.out.println("""res0: Boolean = """ + $show(res$0));$skip(15); val res$1 = 
string.isEmpty;System.out.println("""res1: Boolean = """ + $show(res$1));$skip(16); val res$2 = 
string.nonEmpty;System.out.println("""res2: Boolean = """ + $show(res$2))}
}
