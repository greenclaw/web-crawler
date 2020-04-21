package com.warehouse

import org.junit.runner.RunWith
import org.scalatest.Outcome
import org.scalatest.flatspec
import org.scalatestplus.junit.JUnitRunner
import com.warehouse.crawler.{MasterCrawler, WebCrawler}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class TestMasterCrawler extends flatspec.AnyFlatSpec {

  behavior of "Empty WebCrawler instance"

  it should "be empty as empty file provided" in {
    val sourcefile = "src/main/resources/empty.txt"
    val outFile    = "/tmp/domains-empty.txt"
    val crawler = new MasterCrawler(sourcefile, outFile)
    assert(crawler.sitesList.isEmpty)
  }

  behavior of "WebCrawler instance"

  it should "be able to load short list of sites from source file properly" in {
    val sourcefile = "src/main/resources/test.txt"
    val outFile    = "/tmp/domains-test.txt"
    val crawler = new MasterCrawler(sourcefile, outFile)
    assert(crawler.sitesList.sameElements(Iterator("twitter.com", "google.com", "yandex.ru")))
  }

  behavior of "WebCrawler instance with medium number of sites"

  it should "be able to load mid number of sites" in {
    val sourcefile = "src/main/resources/medium.txt"
    val outFile    = "/tmp/domains-medium.txt"
    val crawler = new MasterCrawler(sourcefile, outFile)

    Await.result(crawler.runCrawlers(), Duration.Inf)
    assert(true)
  }

  behavior of "WebCrawler instance with large number of sites"

  it should "be able to load mid number of sites" in {
    val sourceFile = "src/main/resources/large.txt"
    val outFile    = "/tmp/domains-large.txt"
    val crawler = new MasterCrawler(sourceFile, outFile)

    Await.result(crawler.runCrawlers(), Duration.Inf)
    assert(true)
  }

}
