package com.warehouse.crawler

import java.util.concurrent.ConcurrentLinkedQueue

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}


/*
  * WebCrawler object is an instance of Crawler, contains methods to:
  *   - download info from sites
  *   - create task to load info
 */
object WebCrawler extends Crawler {

  val log = LoggerFactory.getLogger(this.getClass)

  def downloadTags(url: String): Tags = {

    import Tags.string2Tags
    tryGetResponse(url) match {
      case Some(response) =>
        s"Site: $url - ok\n$response\n"
      case None =>
        log.info("Get response to url has no response")
        s"Site: $url - no response"
    }
  }

  def downloadString(url: String): String = {
    tryGetResponse(url) match {
      case Some(response) =>
        s"Site: $url - ok\n$response\n"
      case None =>
        log.info("Get response to url has no response")
        s"Site: $url - no response"
    }
  }

  def build(url: String, id: Int, queue: ConcurrentLinkedQueue[String])(implicit ec: ExecutionContext): Future[Unit] =
    Future[Unit] {
//      log.info(s"Starting task (id=$id, url=$url)")
      queue.offer(downloadString(url))
      log.info(s"Task finished (id=$id, url=$url)")

    }
}
