package me.socure.themelio.core

import scala.language.higherKinds

trait Map[Res[+_, +_]] {
  def map[Err, Out, Out1](res: Res[Err, Out], f: Out => Out1): Res[Err, Out1]
}
