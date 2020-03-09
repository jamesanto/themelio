package me.socure.themelio.core

import scala.language.higherKinds

trait FlatMapFailure[Res[+_, +_]] {
  def flatMapFailure[Err, Out, Err1](res: Res[Err, Out], f: Err => Res[Err1, Out]): Res[Err1, Out]
}
