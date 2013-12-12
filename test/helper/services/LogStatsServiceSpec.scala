package helper.services

import org.specs2.mutable._
import esclient.Elasticsearch
import play.api.test.WithApplication

class LogStatsServiceSpec extends Specification {

  "LogStatsService" should {

    "indexIsLogIndex should return true if indexname is from a logindex" in new WithApplication {
      val logStatsService: LogStatsService = new LogStatsService(new Elasticsearch)
      logStatsService.indexIsLogIndex("fbl_bla_log") must beTrue
    }

    "indexIsLogIndex should return false if indexname is not from a logindex" in new WithApplication {
      val logStatsService: LogStatsService = new LogStatsService(new Elasticsearch)
      logStatsService.indexIsLogIndex("fbl_bla") must beFalse
    }

    "indexIsLogIndex should return false if indexname is not from a logindex" in new WithApplication {
      val logStatsService: LogStatsService = new LogStatsService(new Elasticsearch)
      logStatsService.indexIsLogIndex("bla_log") must beFalse
    }
  }

}
