package com.warehouse

import com.warehouse.crawler.MasterCrawler


object Main extends App {

  // Simple example
  val sourceFile = "src/main/resources/test.txt"
  val targetFile = "/tmp/domains-test.txt"
  val master = new MasterCrawler(sourceFile, targetFile)

  master.runCrawlers()

//  println("Finished")
}
