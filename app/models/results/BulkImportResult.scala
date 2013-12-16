package models.results

case class BulkImportResult(error: Boolean, failures: Int, requests: Int)