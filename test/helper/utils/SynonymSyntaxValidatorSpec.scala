package helper.utils

import org.specs2.mutable.Specification

class SynonymSyntaxValidatorSpec extends Specification {

  "SynonymSyntaxValidator" should {
    "getIncorrectSyntaxLineNo should return line number of syntax error" in {
      SynonymSyntaxValidator.getIncorrectSyntaxLineNo("test,test\r\ntest") must beEqualTo(2)
    }

    "getIncorrectSyntaxLineNo should return 0 for correct code" in {
      SynonymSyntaxValidator.getIncorrectSyntaxLineNo("test,test\r\ntest=>test") must beEqualTo(0)
    }

    "isWrongSyntax should return true for incorrect code" in {
      SynonymSyntaxValidator.isWrongSyntax("test, test\r\ntest") must beTrue
      SynonymSyntaxValidator.isWrongSyntax("test=>test\r\ntest") must beTrue
      SynonymSyntaxValidator.isWrongSyntax("test") must beTrue
    }

    "isWrongSyntax should return false for correct code" in {
      SynonymSyntaxValidator.isWrongSyntax("test, test\r\ntest, test") must beFalse
      SynonymSyntaxValidator.isWrongSyntax("test=>test\r\ntest, test") must beFalse
      SynonymSyntaxValidator.isWrongSyntax(" ") must beFalse
    }

    "getIncorrectSyntaxLine must return an empty String or the wrong line" in {
      SynonymSyntaxValidator.getIncorrectSyntaxLine("test=>test\r\ntest") must beEqualTo("test")
      SynonymSyntaxValidator.getIncorrectSyntaxLine("test=>test\r\ntest=>test") must beEqualTo("")
    }

    "getIncorrectSyntaxLine must return an empty String or the wrong line" in {
      SynonymSyntaxValidator.isCorrectLine("test=>test") must beTrue
      SynonymSyntaxValidator.isCorrectLine("testtest") must beFalse
      SynonymSyntaxValidator.isCorrectLine("test, test") must beTrue
      SynonymSyntaxValidator.isCorrectLine(" ") must beTrue
    }
  }
}
