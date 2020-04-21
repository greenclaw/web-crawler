package com.warehouse.crawler

import java.util.concurrent.FutureTask
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

/*
  * <code>Cancellable</code> is the wrapper of <code>FutureTask</code> which provides cancel method
 */
class Cancellable[T](task: => T, executionContext: ExecutionContext) {
  private val p = Promise[T]()

  def future: Future[T] = p.future

  private val futureTask: FutureTask[T] = new FutureTask[T](() => task) {
    override def done() = p.complete(Try(get()))
  }

  def cancel(): Unit = futureTask.cancel(true)

  executionContext.execute(futureTask)
}

object Cancellable {
  def apply[T](todo: => T)(implicit executionContext: ExecutionContext): Cancellable[T] =
    new Cancellable[T](todo, executionContext)
}
