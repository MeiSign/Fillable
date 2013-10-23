object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json

val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                                  //> format  : java.text.SimpleDateFormat = java.text.SimpleDateFormat@fe8ba6fa
format.parse("2013-09-17T12:50:20Z")              //> res0: java.util.Date = Tue Sep 17 12:50:20 CEST 2013
Math.pow(2,31)                                    //> res1: Double = 2.147483648E9
}