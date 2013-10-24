object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import java.security.MessageDigest

val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                                  //> format  : java.text.SimpleDateFormat = java.text.SimpleDateFormat@fe8ba6fa
format.parse("2013-09-17T12:50:20Z")              //> res0: java.util.Date = Tue Sep 17 12:50:20 CEST 2013
Math.pow(2,31)                                    //> res1: Double = 2.147483648E9
"hallo".hashCode()                                //> res2: Int = 99043158
"hallo".hashCode()                                //> res3: Int = 99043158
"hallo welt!1@".hashCode()                        //> res4: Int = 1046042288
"hallo welt!1@? hallo welt!1@ sdfsfsdf test".hashCode()
                                                  //> res5: Int = -1643429788
"-1643429788".hashCode()                          //> res6: Int = 870563437
MessageDigest.getInstance("MD5").digest("hallo welt!1@".getBytes())
                                                  //> res7: Array[Byte] = Array(-76, -12, 26, -71, 58, 102, -34, -124, -125, -60, 
                                                  //| 8, -39, -120, 89, 65, 29)
MessageDigest.getInstance("MD5").digest("hallo welt!1@".getBytes())
                                                  //> res8: Array[Byte] = Array(-76, -12, 26, -71, 58, 102, -34, -124, -125, -60, 
                                                  //| 8, -39, -120, 89, 65, 29)
}