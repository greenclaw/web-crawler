package com.warehouse.crawler

import java.util.concurrent.{ConcurrentLinkedQueue, Executors, TimeUnit}

import com.warehouse.crawler.Control.using
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/*
  * <code>MasterCrawler</code> interface to start threads to download the sites content
  * and save it to provided file location
 */
class MasterCrawler(sourceFile: String, targetFile: String) {

  val log = LoggerFactory.getLogger(this.getClass)

  val sitesList: List[String] = loadSiteList(sourceFile)

  /*
    * Tasks are not CPU bounded, but IO bounded
    * Thus, it's better tweak number of threads
   */
  val minionsToAllocate: Int = {
    val cores = Runtime.getRuntime.availableProcessors()
    if (sitesList.length / cores <= 32) {
      cores
    } else {
      cores * 128
    }
  }

  val queue = new ConcurrentLinkedQueue[String]()

  private val executor = Executors.newFixedThreadPool(minionsToAllocate)
  implicit private val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

  private val scheduler = Executors.newScheduledThreadPool(1)
  private val backgroundFlusher = scheduler
    .scheduleWithFixedDelay(buildFlusher(),5, 5, TimeUnit.SECONDS)
  private val flusherContext = ExecutionContext.fromExecutor(scheduler)

  /*
    * Start crawlers in sequence along with background flusher
    * At the end dumps all remaining tags from queue
   */
  def runCrawlers(): Future[List[Unit]] = {
    WebCrawler.createNewFile(targetFile)

    val futures = Future.sequence(
      sitesList.zipWithIndex.map{
        case (url, id) => WebCrawler.build(url, id, queue)(ec)
      }
    )
    futures.onComplete( _ => {
      backgroundFlusher.cancel(true)
      scheduler.shutdownNow()
      Await.result(buildFinalizer(), Duration.Inf)
      executor.shutdownNow()
    })
    futures
  }

  // Polls bunch of elements from queue and dumps to disk
  private def flush(batchSize: Int): Unit = {
    WebCrawler.writeMultipleToFile((0 until batchSize).map { _ =>
      queue.poll()
    }, targetFile)
  }

  /*
    Background job flusher
    Poll the queue and store info to file on disk
   */
  private def buildFlusher() = new Runnable {
    def run(): Unit = {
      log.info("Try to flush")
      flush(queue.size)
    }
  }


  /*
    Final job dumps remaining tags in queue
   */
  private def buildFinalizer() =
    Future[Unit] {
      log.info("Final flush")
      flush(queue.size)
    }(ec)


  private def loadSiteList(fileString: String): List[String] = {
    using(io.Source.fromFile(fileString)) { source =>
      source.getLines().toList
    }
  }

}
