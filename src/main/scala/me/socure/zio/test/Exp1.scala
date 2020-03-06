package me.socure.zio.test

import java.util.concurrent.atomic.AtomicInteger

import zio.ZIO
import zio.clock.Clock
import zio.duration.Duration.Finite
import zio.stream._

object Exp1 {

  import ZioSupport._

  def main1(args: Array[String]): Unit = {
    val s = Stream.fromIterator(ZIO.succeed(Iterator(1, 2, 3)))
    out(s.map(_ * 2))
    out(s.flatMap(i => ZStream(s"A_$i", s"B_$i")))
  }

  private val counter = new AtomicInteger()

  def main(args: Array[String]): Unit = {
    val s = Stream
      .fromEffect(ZIO.effect({
        val l = List(counter.incrementAndGet(), counter.incrementAndGet(), counter.incrementAndGet())
        log(s"Producing $l")
        l
      }).delay(Finite(500000)).provide(Clock.Live))
      .flatMap(Stream.fromIterable)
      .forever
      .takeWhile(_ < 10)

    val res = s.mapMParUnordered(3)(i => ZIO.effect(log(s"Hello $i")).delay(Finite(1000000000))).foreach(_ => ZIO.unit)
    log(runtime.unsafeRun(res))
  }
}
