package com.warehouse.crawler

import java.nio.file.{Files, Paths, StandardOpenOption}

object CrawlerDefaults {
  val LIMIT: Int = 5 * 1024     // response limit
  val HTTP_SCHEMA = "http"
  val HTTPS_SCHEMA = "https"
  val RETRY_TIMES = 1           // times to retry request for each schemas
}

/*
  * Crawler trait is the interface containing methods to retrieve and gather information
 */
trait Crawler {

  import CrawlerDefaults._
  import scalaj.http._


  def createNewFile(filePath: String): Unit = {
    val path = Paths.get(filePath)
    Files.deleteIfExists(path)
    Files.createFile(path)
  }


  def writeToFile(tags: Tags, filePath: String): Unit = {
    Files.write(Paths.get(filePath),
                tags.toString.getBytes("UTF-8"),
                StandardOpenOption.APPEND)
  }

  def writeMultipleToFile(tagsList: Seq[String], filePath: String): Unit = {
    Files.write(Paths.get(filePath),
                tagsList.mkString("\n\n").getBytes("UTF-8"),
                StandardOpenOption.APPEND)
  }

  /*
    * Method gets info from site and cut first 5Kb
   */
  @throws(classOf[java.io.IOException])
  def getWithLimit(url: String, limit: Option[Int] = None): String = {
    Http(url)
      .timeout(connTimeoutMs = 2000, readTimeoutMs = 5000)
      .headers(Map("User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5)"))
      .option(HttpOptions.followRedirects(true))
      .option(HttpOptions.allowUnsafeSSL)
      .asString.body.iterator
        .take(limit.getOrElse(LIMIT))
        .mkString
  }

  /*
    * Wraps url with appropriate protocol prefix
   */
  def wrapProtocol(url: String, safe: Boolean = false): String = {
    if (!safe) s"${HTTP_SCHEMA}://${url}"
    else s"${HTTPS_SCHEMA}://${url}"
  }

  def tryGetResponse(url: String, safe: Boolean = false, retry: Int = 0): Option[String] = {
    try  {
      // Try unsafe protocol, plain http
      Some(getWithLimit(wrapProtocol(url, safe=safe)))
    } catch {
      case e: Exception =>
        if (retry < CrawlerDefaults.RETRY_TIMES) {
//          println(s"Exception was observed during handling request:")
//          println(s"Params: url=$url, safe=$safe, retry=$retry")
          tryGetResponse(url, safe, retry + 1)
        } else {
          if (!safe) {
            tryGetResponse(url, safe=true)
          } else {
            None
          }
        }
    }
  }
}

object Crawler extends Crawler
