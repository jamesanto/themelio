package me.socure.zio.test


import monix.eval.Task
import monix.execution.atomic.AtomicInt
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.Observable
import monix.reactive.observables.ConnectableObservable
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.ConcurrentSubject

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

object Monix1 {
  private implicit val scheduler: Scheduler = Scheduler.Implicits.global

  class ObservableAfterAllSupport[A](
                                      val underlying: Observable[A],
                                      cb: A => Task[Unit]
                                    ) extends Observable[A] {

    private val numSubscribers = AtomicInt(0)
    private val counter = TrieMap[A, AtomicInt]()

    val modifiedUnderlying: Observable[A] = underlying.doOnNextAck { (element, ack) =>
      Task
        .fromFuture(ack)
        .flatMap { _ =>
          val numConsumed = counter.getOrElseUpdate(element, AtomicInt(0)).incrementAndGet()
          println(s"INC : [$element] [$numConsumed] [${numSubscribers.get()}]")
          if (numConsumed >= numSubscribers.get()) {
            cb(element)
          } else Task.unit
        }.onErrorHandle { _ =>
        counter.remove(element)
      }
    }

    override def unsafeSubscribeFn(subscriber: Subscriber[A]): Cancelable = {
      numSubscribers.increment()
      modifiedUnderlying.unsafeSubscribeFn(subscriber)
    }
  }

  implicit class RichObservable[+A](val value: Observable[A]) extends AnyVal {
    def afterAllConsumers(cb: A => Task[Unit]): Observable[A] = new ObservableAfterAllSupport[A](value, cb)
  }

  def cached[A](data: Observable[A])(implicit scheduler: Scheduler): Observable[A] = {
    val subject = ConcurrentSubject.replay[A]
    val cachedData = ConnectableObservable.cacheUntilConnect(data, subject)
    cachedData.connect()
    cachedData
  }

  def main(args: Array[String]): Unit = {

    val source = cached(
      Observable(1, 2, 3)
        .afterAllConsumers(i => Task(println(s"on next : $i")))
        .delayExecution(500.milliseconds)
    )

    source.foreachL(println).runAsyncAndForget
    source.foreachL(println).runAsyncAndForget
    Thread.sleep(5000)
  }
}
