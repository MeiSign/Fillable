package esclient

import play.api.Play
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import collection.JavaConversions._
import play.api.i18n.Messages
import play.api.http.Writeable

case class EsClient(val requestHolder: Option[WS.WSRequestHolder]) {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val optHosts: Option[List[String]] = Play.current.configuration.getStringList("esclient.url").map(_.toList)
  var hosts: List[String] = optHosts.getOrElse(List(Messages("error.noHostConfig")))

  def execute(query: EsQuery): Future[Response] = {
    query.httpType match {
      case HttpType.Get => get(query)
      case HttpType.Post => post(query)
      case HttpType.Put => put(query)
      case HttpType.Head => head(query)
      case HttpType.Delete => delete(query)
      case _ => get(query)
    }
  }


  def delete(query: EsQuery): Future[Response] = {
    requestHolder.getOrElse(WS.url(getUrl(query))).delete()
  }

  def head(query: EsQuery): Future[Response] = {
    requestHolder.getOrElse(WS.url(getUrl(query))).head()
  }

  def put(query: EsQuery): Future[Response] = {
    requestHolder.getOrElse(WS.url(getUrl(query))).put(query.toJson)
  }

  def post(query: EsQuery): Future[Response] = {
    requestHolder.getOrElse(WS.url(getUrl(query))).post(query.toJson)
  }

  def get(query: EsQuery): Future[Response] = {
    requestHolder.getOrElse(WS.url(getUrl(query))).get()
  }

  def getUrl(query: EsQuery ): String = {
    if (hosts.isEmpty)
      ("http://" + Messages("error.noHostConfig"))
    else {
      hosts = hosts.tail ++ List(hosts.head)
      completeUrl(hosts(0)) + query.getUrlAddon
    }
  }

  def completeUrl(url: String) = if (url.startsWith("http://")) url else "http://" + url
}

object EsClient {
  val client = new EsClient(Option.empty)

  def execute(query: EsQuery) = client.execute(query)
  def url(query: EsQuery) = client.getUrl(query)
}