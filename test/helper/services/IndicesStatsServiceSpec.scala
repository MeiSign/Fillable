package helper.services

import org.specs2.mutable.Specification
import play.api.test.{FakeApplication, WithApplication}
import esclient.Elasticsearch

class IndicesStatsServiceSpec extends Specification {

  val fakeApp = new FakeApplication(additionalConfiguration = Map(
    "esclient.embeddedElasticsearch" -> false,
    "esclient.clustername" -> "testcluster123"
  ))

  "IndicesStatsServiceSpec" should {
    "isFillableIndex should return true for indexname" in new WithApplication(fakeApp){
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("fbl_test") must beTrue
    }

    "isFillableIndex should return false for fillable log index" in new WithApplication(fakeApp){
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("fbl_test_log") must beFalse
    }

    "isFillableIndex should return false for normal es index" in new WithApplication(fakeApp){
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("test") must beFalse
    }
  }

}
