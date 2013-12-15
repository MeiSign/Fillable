package helper.utils

import org.specs2.mutable.Specification
import play.api.test.WithApplication

class IndexNameValidatorSpec extends Specification {

  "IndexNameValidatorSpec" should {
    "containsOnlyValidChars return true for valid index names" in {
      IndexNameValidator.containsOnlyValidChars("dsfgdsfgsd12_bcr0") must beTrue
    }

    "containsOnlyValidChars return false for invalid index names" in {
      IndexNameValidator.containsOnlyValidChars("SD!GSDF12_bcr0") must beFalse
    }
  }
}
