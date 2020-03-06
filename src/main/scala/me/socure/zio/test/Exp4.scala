package me.socure.zio.test

import zio.ZIO
import zio.stream._

object Exp4 extends ZioSupport {
  def main(args: Array[String]): Unit = {
    val s = Stream.fromIterator(ZIO.effect((1 to 3).iterator))
    val res = Stream.managed(s.broadcast(2, 5)).map(_.zipWithIndex.map { case (s, i) =>
      s.map(v => s"[$i] $v")
    }).mapConcat(identity).flatMap(identity)
//    val s1 = s.broadcast(2, 5).map { l =>
//      Stream.mergeAll(2)(
//        l.head.map(i => s"A : $i"),
//        l.last.map(i => s"B : $i")
//      )
//    }
    out(res)
  }
}
