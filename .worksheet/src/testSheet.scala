object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(156); 

val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");System.out.println("""format  : java.text.SimpleDateFormat = """ + $show(format ));$skip(37); val res$0 = 
format.parse("2013-09-17T12:50:20Z");System.out.println("""res0: java.util.Date = """ + $show(res$0));$skip(15); val res$1 = 
Math.pow(2,31);System.out.println("""res1: Double = """ + $show(res$1))}
}
