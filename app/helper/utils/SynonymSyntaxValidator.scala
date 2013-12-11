package helper.utils

object SynonymSyntaxValidator {

  def getIncorrectSyntaxLineNo(text: String) : Int = {
    val lines = text.split("\r\n")
    lines.indexWhere(line => !isCorrectLine(line)) + 1
  }

  def getIncorrectSyntaxLine(text: String) : String = {
    val lines = text.split("\r\n")
    lines.indexWhere(line => !isCorrectLine(line)) match {
      case -1 => ""
      case i: Int => lines(i)
      case _ => ""
    }
  }

  def isWrongSyntax(text: String) : Boolean = {
    val lines = text.split("\r\n")
    !lines.forall(line => isCorrectLine(line))
  }

  def isCorrectLine(line:String) = line.contains("=>") || line.contains(",") || line.trim.isEmpty
}
