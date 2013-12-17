package models.results

case class EditSynonymsResult(error: String = "", reindexResult: ReindexResult = ReindexResult(0, 0)) {
  def hasError = !error.isEmpty
}