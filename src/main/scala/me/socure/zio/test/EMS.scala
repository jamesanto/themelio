package me.socure.zio.test

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.Future
import scala.concurrent.duration._

object EMS {

  private implicit val scheduler: Scheduler = Scheduler.Implicits.global

  case class ErrorResponse()

  case class Request()

  case class Response()

  def call(req: Request): Future[Either[ErrorResponse, Response]] = ???

  def call(requests: List[Request]): Future[List[Either[ErrorResponse, Response]]] = {
    Observable
      .fromIterable(requests)
      .bufferTumbling(5)
      .mapEval { requestsBatch =>
        Task
          .gatherN(5)(
            requestsBatch.map(req => Task.fromFuture(call(req)))
          )
          .map(Observable.fromIterable)
          .delayExecution(1.second)
      }
      .flatten
      .toListL
      .runToFuture
  }
}
