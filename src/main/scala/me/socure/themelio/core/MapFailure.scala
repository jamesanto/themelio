package me.socure.themelio.core

trait MapFailure[Res[_, _]] {
  def mapFailure[Err, Out, Err1](res: Res[Err, Out], f: Err => Err1): Res[Err1, Out]
}
