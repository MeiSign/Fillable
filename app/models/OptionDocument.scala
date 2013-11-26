package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import org.elasticsearch.common.xcontent.{ToXContent, XContentBuilder}
import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.action.get.GetResponse

case class OptionDocument(input: String = "", output: String = "", weight: Int = 0) {
  def toJsonBuilder: XContentBuilder = jsonBuilder()
      .startObject()
      .startObject("fillableOptions")
      .field("input", input)
      .field("output", output)
      .field("weight", weight)
      .endObject()
      .endObject()

def fromBuilder(doc: GetResponse): OptionDocument = {
  val docBuilder: XContentBuilder = jsonBuilder()
  val xContentString = doc.toXContent(docBuilder, ToXContent.EMPTY_PARAMS).string()
  val json = Json.parse(xContentString)
  OptionDocument((json \ "_source" \ "fillableOptions" \ "input").as[String],
    (json \ "_source" \ "fillableOptions" \ "output").as[String],
    (json \ "_source" \ "fillableOptions" \ "weight").as[Int])
}

def extendOption() = OptionDocument(input, output, weight + 1)

}

object OptionDocument {
  implicit val reader = (
    (__ \ "_source" \ "fillableOptions" \ "input").read[String] and
    (__ \ "_source" \ "fillableOptions" \ "output").read[String] and
    (__ \ "_source" \ "fillableOptions" \ "weight").read[Int]) {
    (input, output, weight) => OptionDocument(input, output, weight)
  }
  (OptionDocument.apply _)
}

