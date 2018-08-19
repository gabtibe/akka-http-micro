package me.gabrieletiberti.microservice.services
import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object CustomExecutorService {
  implicit val executorService = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(2000)
    override def execute(runnable: Runnable): Unit = threadPool.submit(runnable)
    override def reportFailure(cause: Throwable): Unit = {}
  }
}
