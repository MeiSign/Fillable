object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import java.security.MessageDigest;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(191); 

val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");System.out.println("""format  : java.text.SimpleDateFormat = """ + $show(format ));$skip(37); val res$0 = 
format.parse("2013-09-17T12:50:20Z");System.out.println("""res0: java.util.Date = """ + $show(res$0));$skip(15); val res$1 = 
Math.pow(2,31);System.out.println("""res1: Double = """ + $show(res$1));$skip(19); val res$2 = 
"hallo".hashCode();System.out.println("""res2: Int = """ + $show(res$2));$skip(19); val res$3 = 
"hallo".hashCode();System.out.println("""res3: Int = """ + $show(res$3));$skip(27); val res$4 = 
"hallo welt!1@".hashCode();System.out.println("""res4: Int = """ + $show(res$4));$skip(56); val res$5 = 
"hallo welt!1@? hallo welt!1@ sdfsfsdf test".hashCode();System.out.println("""res5: Int = """ + $show(res$5));$skip(25); val res$6 = 
"-1643429788".hashCode();System.out.println("""res6: Int = """ + $show(res$6));$skip(68); val res$7 = 
MessageDigest.getInstance("MD5").digest("hallo welt!1@".getBytes());System.out.println("""res7: Array[Byte] = """ + $show(res$7));$skip(68); val res$8 = 
MessageDigest.getInstance("MD5").digest("hallo welt!1@".getBytes());System.out.println("""res8: Array[Byte] = """ + $show(res$8))}
}
