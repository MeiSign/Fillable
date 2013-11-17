package helper

object IndexNameValidator {

  val validIndexChars = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ List('_')).toSet

  def containsOnlyValidChars(name: String): Boolean = name.forall(validIndexChars.contains(_))
}
