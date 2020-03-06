package me.socure.themelio.core

trait MapSuccess[Res[_, _]] {
  def mapSuccess[Err, Out, Out1](res: Res[Err, Out], f: Out => Out1): Res[Err, Out1]
}
