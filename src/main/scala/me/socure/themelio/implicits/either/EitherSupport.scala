package me.socure.themelio.implicits.either

import me.socure.themelio.core.{MapFailure, MapSuccess}

trait EitherSupport {
  implicit val mapSuccess: MapSuccess[Either] = new MapSuccess[Either] {
    override def mapSuccess[Err, Out, Out1](res: Either[Err, Out], f: Out => Out1): Either[Err, Out1] = res.right.map(f)
  }

  implicit val mapFailure: MapFailure[Either] = new MapFailure[Either] {
    override def mapFailure[Err, Out, Err1](res: Either[Err, Out], f: Err => Err1): Either[Err1, Out] = res.left.map(f)
  }
}
