object testSheet {
import play.api.libs.json.JsValue
import play.api.libs.json.Json

val response: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0}}""")
                                                  //> response  : play.api.libs.json.JsValue = {"_shards":{"total":5,"successful":
                                                  //| 5,"failed":0}}
val string: Seq[JsValue] = (response \\ "options")//> string  : Seq[play.api.libs.json.JsValue] = List()
string.to[List]                                   //> res0: List[play.api.libs.json.JsValue] = List()

val response1: JsValue = Json.parse("""{"_shards":{"total":5,"successful":5,"failed":0},"hotels":[{"text":"m","offset":0,"length":1,"options":[{"text":"Hotel Marriot","score":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","score":5.0}]}]}""")
                                                  //> response1  : play.api.libs.json.JsValue = {"_shards":{"total":5,"successful"
                                                  //| :5,"failed":0},"hotels":[{"text":"m","offset":0,"length":1,"options":[{"text
                                                  //| ":"Hotel Marriot","score":15.0},{"text":"Hotel Monaco","score":10.0},{"text"
                                                  //| :"Hotel Mercure","score":5.0}]}]}
val string1: Seq[JsValue] = (response1 \\ "options")
                                                  //> string1  : Seq[play.api.libs.json.JsValue] = List([{"text":"Hotel Marriot","
                                                  //| score":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","s
                                                  //| core":5.0}])
string1.to[List]                                  //> res1: List[play.api.libs.json.JsValue] = List([{"text":"Hotel Marriot","scor
                                                  //| e":15.0},{"text":"Hotel Monaco","score":10.0},{"text":"Hotel Mercure","score
                                                  //| ":5.0}])

}