package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class OptionDocument(input: List[String] = List[String](), output: String = "", weight: Int = 0) {
  def isEmpty = output.equals("") && input.equals(List[String]()) && weight == 0
  override def toString = "Output: " + output + ", Input: " + input.toString + ", " + "Weight: " + weight
}

object OptionDocument {
  implicit val reader = (
    (__ \ "_source" \ "fillableOptions" \ "input").read[List[String]] and
    (__ \ "_source" \ "fillableOptions" \ "output").read[String] and
    (__ \ "_source" \ "fillableOptions" \ "weight").read[Int]) {
    (input, output, weight) => OptionDocument(input, output, weight)
  }
  (OptionDocument.apply _)
}

