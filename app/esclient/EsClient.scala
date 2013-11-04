package esclient

import play.api.Play
import scala.concurrent.Future
import play.api.libs.ws._
import play.api.libs.ws.Response
import collection.JavaConversions._
import scala.collection.immutable.Queue

object EsClient {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val optHosts: Option[List[String]] = Play.current.configuration.getStringList("esclient.url").map(_.toList)
  var hosts: List[String] = optHosts.getOrElse(List("No host specified in Configuration"))

  def execute(query: EsQuery): Future[Response] = {
    query.httpType match {
      case HttpType.Get => WS.url(url + query.getUrlAddon).get()
      case HttpType.Post => WS.url(url + query.getUrlAddon).post(query.toJson)
      case HttpType.Put => WS.url(url + query.getUrlAddon).put(query.toJson)
      case HttpType.Head => WS.url(url + query.getUrlAddon).head
      case HttpType.Delete => WS.url(url + query.getUrlAddon).delete
      case _ => WS.url(url + query.getUrlAddon).get()
    }
  }
  
  def url: String = {
    hosts = hosts.tail ++ List(hosts.head)
    if (hosts(0).startsWith("http://")) hosts(0)
    else "http://" + hosts(0)
  }
  
}