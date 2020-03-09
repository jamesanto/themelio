package me.socure.themelio.core

import scala.language.higherKinds

trait FlatMap[Res[+_, +_]] {
  def flatMap[Err, Out, Out1](res: Res[Err, Out], f: Out => Res[Err, Out1]): Res[Err, Out1]
}
