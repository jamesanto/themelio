package me.socure.themelio.core

trait FlatMapSuccess[Res[_, _]] {
  def flatMapSuccess[Err, Out, Out1](res: Res[Err, Out], f: Out => Res[Err, Out1]): Res[Err, Out1]
}
