object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(176); 

val response: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0}}""");System.out.println("""response  : play.api.libs.json.JsValue = """ + $show(response ));$skip(51); 
val string: Seq[JsValue] = (response \\ "options");System.out.println("""string  : Seq[play.api.libs.json.JsValue] = """ + $show(string ));$skip(16); val res$0 = 
string.to[List];System.out.println("""res0: List[play.api.libs.json.JsValue] = """ + $show(res$0));$skip(264); 

val response1: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0},"hotels":[{"text":"m","offset":0,"length":1,"options":[{"text":"Hotel Marriot","score":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","score":5.0}]}]}""");System.out.println("""response1  : play.api.libs.json.JsValue = """ + $show(response1 ));$skip(53); 
val string1: Seq[JsValue] = (response1 \\ "options");System.out.println("""string1  : Seq[play.api.libs.json.JsValue] = """ + $show(string1 ));$skip(17); val res$1 = 
string1.to[List];System.out.println("""res1: List[play.api.libs.json.JsValue] = """ + $show(res$1))}

}
