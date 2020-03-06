package me.socure.zio.test

import zio.ZIO
import zio.stream._

object Exp5 extends ZioSupport {
  def main(args: Array[String]): Unit = {
    val s = Stream.fromIterator(ZIO.succeed(Iterator(1, 2, 3)))
    out(s.map(i => s"A : $i"))
    out(s.map(i => s"B : $i"))
  }
}
