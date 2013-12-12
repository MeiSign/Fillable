package helper.services

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import esclient.Elasticsearch

class IndicesStatsServiceSpec extends Specification {

  "IndicesStatsServiceSpec" should {
    "isFillableIndex should return true for indexname" in new WithApplication {
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("fbl_test") must beTrue
    }

    "isFillableIndex should return false for fillable log index" in new WithApplication {
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("fbl_test_log") must beFalse
    }

    "isFillableIndex should return false for normal es index" in new WithApplication {
      val indicesStatsService: IndicesStatsService = new IndicesStatsService(new Elasticsearch)
      indicesStatsService.isFillableIndex("test") must beFalse
    }
  }

}
