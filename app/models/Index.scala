package models

case class Index(name: String, shards: Int = 4, replicas: Int = 0)