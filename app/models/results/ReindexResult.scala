package models.results

case class ReindexResult(failures: Int, requests: Int) {
  def succeeded: Int = requests - failures
}