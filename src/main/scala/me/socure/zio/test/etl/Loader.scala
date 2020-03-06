package me.socure.zio.test.etl

import monix.eval.Task
import monix.reactive.Observable

trait Loader[A] {
  def load(data: Observable[A]): Task[Unit]
}
