package me.socure.themelio.core

trait Generalizer[In1, In2, Out] {
  def generalize1(in1: In1): Out

  def generalize2(in2: In2): Out
}

object Generalizer {
  def eitherGeneralizer[In1, In2]: Generalizer[In1, In2, Either[In1, In2]] = new Generalizer[In1, In2, Either[In1, In2]] {
    override def generalize1(in1: In1): Either[In1, In2] = Left(in1)

    override def generalize2(in2: In2): Either[In1, In2] = Right(in2)
  }

  object Implicits {
    implicit def eitherGeneralizerImp[In1, In2]: Generalizer[In1, In2, Either[In1, In2]] = eitherGeneralizer[In1, In2]
  }

}
