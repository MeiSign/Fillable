package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication

@RunWith(classOf[JUnitRunner])
class OptionDocumentSpec extends Specification {

  "OptionDocument" should {
    "isEmpty should return true for empty documents" in new WithApplication {
      OptionDocument().isEmpty must beTrue
    }
  }

  "OptionDocument" should {
    "isEmpty should return false for nonempty documents" in new WithApplication {
      OptionDocument(List("bla"), "blub", 1).isEmpty must beFalse
    }
  }
}
