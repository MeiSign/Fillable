package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Index(name: String, shards: Int = 4, replicas: Int = 0)

object Index {
  implicit val reader = (
    (__ \ "name").read[String] and
    (__ \ "shards").read[Int] and
    (__ \ "replicas").read[Int]) {
      (indexname, shards, replicas) => Index(indexname, shards, replicas)
    }
  (Index.apply _)
}