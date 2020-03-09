package me.socure.themelio.implicits.result

import me.socure.themelio.core._

trait ResultSupport {
  implicit val mapRes: Map[Result] = new Map[Result] {
    override def map[Err, Out, Out1](res: Result[Err, Out], f: Out => Out1): Result[Err, Out1] = res.map(f)
  }

  implicit val flatMapRes: FlatMap[Result] = new FlatMap[Result] {
    override def flatMap[Err, Out, Out1](res: Result[Err, Out], f: Out => Result[Err, Out1]): Result[Err, Out1] = res.flatMap(f)
  }

  implicit val mapFailureRes: MapFailure[Result] = new MapFailure[Result] {
    override def mapFailure[Err, Out, Err1](res: Result[Err, Out], f: Err => Err1): Result[Err1, Out] = res.mapFailure(f)
  }

  implicit val flatMapFailureRes: FlatMapFailure[Result] = new FlatMapFailure[Result] {
    override def flatMapFailure[Err, Out, Err1](res: Result[Err, Out], f: Err => Result[Err1, Out]): Result[Err1, Out] = res.flatMapFailure(f)
  }
}
