package me.socure.zio.test.etl

import monix.eval.Task
import monix.reactive.Observable

trait Transformer[-In, +Out] {
  def transform(source: Observable[In]): Task[Observable[Out]]
}
