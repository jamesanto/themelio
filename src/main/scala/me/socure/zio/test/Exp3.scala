package me.socure.zio.test

import zio.ZIO
import zio.stream._

object Exp3 {

  import ZioSupport._

  def main(args: Array[String]): Unit = {
    val s = Stream.fromIterable(Iterable(1, 2, 3)).broadcast(2, 10).use {
      case s1 :: s2 :: Nil =>

//        val s1o = s1.foreach(i => ZIO.effect(log(s"A : $i")))
//        val s2o = s2.foreach(i => ZIO.effect(log(s"B : $i")))
//
//        s1o <&> s2o

        val s1Out = s1.map(i => s"A : $i")
        val s2Out = s2.map(i => s"B : $i")
        ZIO.effect(s1Out ++ s2Out)
    }
    out(s)
  }
}
