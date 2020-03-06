package me.socure.zio.test.etl

import monix.eval.Task
import monix.reactive.Observable

trait Extractor[+A] {
  def extract(): Task[Observable[A]]
}
