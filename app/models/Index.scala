package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Index(name: String, shards: Int = 4, replicas: Int = 0)