package esclient.queries

import play.api.libs.json.JsObject
import esclient.EsQuery
import esclient.HttpType
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import play.api.i18n.Messages

class GetEsVersionQuery extends EsQuery {
 val httpType = HttpType.Get
  
  def getUrlAddon: String = "/"
  
  def toJson: JsObject = Json.obj()
  
  def getResult(response: JsValue): JsObject = {
    require(response != null, "response JsValue must not be null")
    
    val version: Option[String] = (response \ "version" \ "number").asOpt[String]
    val buildTimestamp: Option[String] = (response \ "version" \ "build_timestamp").asOpt[String]
    
    if (version.isDefined && buildTimestamp.isDefined) {
      try {
        respondSuccess(Json.obj("version" -> version.get, "fullfillsRequirements" -> isAfterOrEqualToRequiredBuildtime(buildTimestamp.get)))
      } catch {
        case e: Exception => e.getMessage match {
          case msg: String if msg.startsWith("Invalid format") => respondError(Messages("error.wrongDateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss'Z'", buildTimestamp.get))
        }
      }
    } else {
      respondError("Could not determine elasticsearch version. Es Cluster up and running?")
    }
  }
 
  def isAfterOrEqualToRequiredBuildtime(buildtime: String): Boolean = {
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    (fmt.parseDateTime(buildtime).isAfter((fmt.parseDateTime("2013-09-17T12:50:20Z"))) || fmt.parseDateTime(buildtime).isEqual((fmt.parseDateTime("2013-09-17T12:50:20Z"))))
  }
}