package helper.services

import scala.concurrent.Future
import org.elasticsearch.Version
import scala.collection.JavaConversions._
import org.elasticsearch.client.Client
import play.api.Play
import esclient.queries.GetEsVersionQuery
import esclient.Elasticsearch

class RequirementsService(es: Elasticsearch) {
  val esClient = es.client
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def runTests: Future[List[Boolean]] = {
    for {
      version <- testElasticsearchVersion
      pwChanged <- testCredentialsChanged
    } yield {
      List(version, pwChanged)
    }
  }

  def testElasticsearchVersion: Future[Boolean] = {
    GetEsVersionQuery(esClient).execute map {
      esVersion => {
        val nodes = esVersion.iterator().toList
        nodes forall(node => node.getVersion.after(Version.V_0_90_5))
      }
    } recover {
      case e: Throwable => false
    }
  }

  def testCredentialsChanged: Future[Boolean] = {
    if (Play.current.configuration.getString("fillable.user").getOrElse("").equals("admin") &&
      Play.current.configuration.getString("fillable.password").getOrElse("").equals("pass123"))
      Future.successful(false)
    else
      Future.successful(true)
  }

}
