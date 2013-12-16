package models.results

import models.InputTopListEntry

case class SynonymResult(topTenInputValues: List[InputTopListEntry] = List.empty[InputTopListEntry],
                         synonymGroups: List[String] = List.empty[String],
                         error: String = "") {

  def hasError : Boolean = !error.isEmpty
}
