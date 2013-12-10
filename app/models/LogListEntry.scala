package models

case class LogListEntry(name: String, logEntries: Long, size: Long) {
  def indexName = name.replace("_log", "")
}