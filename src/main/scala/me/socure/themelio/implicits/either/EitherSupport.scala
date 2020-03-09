package me.socure.themelio.implicits.either

import me.socure.themelio.core.{FlatMapFailure, FlatMap, MapFailure, Map}

trait EitherSupport {
  implicit val mapSuccess: Map[Either] = new Map[Either] {
    override def map[Err, Out, Out1](res: Either[Err, Out], f: Out => Out1): Either[Err, Out1] = res.right.map(f)
  }

  implicit val mapFailure: MapFailure[Either] = new MapFailure[Either] {
    override def mapFailure[Err, Out, Err1](res: Either[Err, Out], f: Err => Err1): Either[Err1, Out] = res.left.map(f)
  }

  implicit val flatMapSuccess: FlatMap[Either] = new FlatMap[Either] {
    override def flatMap[Err, Out, Out1](res: Either[Err, Out], f: Out => Either[Err, Out1]): Either[Err, Out1] = {
      res.right.flatMap(f)
    }
  }

  implicit val flatMapFailure: FlatMapFailure[Either] = new FlatMapFailure[Either] {
    override def flatMapFailure[Err, Out, Err1](res: Either[Err, Out], f: Err => Either[Err1, Out]): Either[Err1, Out] = {
      res.left.flatMap(f)
    }
  }
}
