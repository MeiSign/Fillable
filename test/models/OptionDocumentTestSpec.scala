package models

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import org.specs2.mock.Mockito
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.common.xcontent.{XContentBuilder, ToXContent}
import org.elasticsearch.common.xcontent.XContentFactory._

class OptionDocumentTestSpec extends Specification with Mockito {

  "toJsonBuilder should return a correct json object" in {
    OptionDocument("input", "output", 5).toJsonBuilder.string() must beEqualTo("""{"fillableOptions":{"input":"input","output":"output","weight":5}}""")
  }

  "extendOption should return a new OptionDocument with increased weight" in {
    OptionDocument("input", "output", 5).extendOption().weight must beEqualTo(6)
  }

  "fromBuilder must parse a GetResponse correctly" in {
    val xContentDoc = jsonBuilder()
      .startObject()
      .startObject("_source")
      .startObject("fillableOptions")
      .field("input", "input")
      .field("output", "output")
      .field("weight", 5)
      .endObject()
      .endObject()
      .endObject()
    val doc = mock[GetResponse].toXContent(any[XContentBuilder], any[ToXContent.Params]).answers(i => xContentDoc)

    OptionDocument().fromBuilder(doc) must beEqualTo(OptionDocument("input", "output", 5))
  }
}
