package com.warehouse

import org.junit.runner.RunWith
import org.scalatest.flatspec
import org.scalatestplus.junit.JUnitRunner

import com.warehouse.crawler.Crawler._

@RunWith(classOf[JUnitRunner])
class TestCrawler extends flatspec.AnyFlatSpec {

  behavior of "Crawler interface methods"

  it should "download first 5kb info from provided site" in {
    val siteurl = "google.com"
    val resp: Option[String] = tryGetResponse(siteurl)
    print(resp)

    assert(resp.getOrElse(None) != None)
    assert(resp.get.length == 5 * 1024)
  }

}
