package me.socure.themelio.implicits.either

import me.socure.themelio.core.{FlatMapFailure, FlatMapSuccess, MapFailure, MapSuccess}

trait EitherSupport {
  implicit val mapSuccess: MapSuccess[Either] = new MapSuccess[Either] {
    override def mapSuccess[Err, Out, Out1](res: Either[Err, Out], f: Out => Out1): Either[Err, Out1] = res.right.map(f)
  }

  implicit val mapFailure: MapFailure[Either] = new MapFailure[Either] {
    override def mapFailure[Err, Out, Err1](res: Either[Err, Out], f: Err => Err1): Either[Err1, Out] = res.left.map(f)
  }

  implicit val flatMapSuccess: FlatMapSuccess[Either] = new FlatMapSuccess[Either] {
    override def flatMapSuccess[Err, Out, Out1](res: Either[Err, Out], f: Out => Either[Err, Out1]): Either[Err, Out1] = {
      res.right.flatMap(f)
    }
  }

  implicit val flatMapFailure: FlatMapFailure[Either] = new FlatMapFailure[Either] {
    override def flatMapFailure[Err, Out, Err1](res: Either[Err, Out], f: Err => Either[Err1, Out]): Either[Err1, Out] = {
      res.left.flatMap(f)
    }
  }
}
