package me.socure.zio.test

import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.atomic.AtomicInt
import monix.reactive.{Consumer, Observable}

import scala.concurrent.duration._

object Monix2 {

  private implicit val scheduler: Scheduler = Scheduler.Implicits.global

  def main(args: Array[String]): Unit = {
    val counter = AtomicInt(0)
    val source = Observable
      .repeat(())
      .map(_ => counter.incrementAndGet()).dump("producing : ")

    val transformed = source.mapEval(i => Task {
      println(s"transforming : $i")
      s"t_$i"
    }.delayExecution(500.milliseconds))
    val consumer = Consumer.foreachTask[String](s => Task(println(s"consuming : $s")).delayExecution(500.milliseconds))
    val result = transformed.consumeWith(consumer)
    result.runSyncUnsafe()
  }
}
