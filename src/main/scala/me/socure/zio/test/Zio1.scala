package me.socure.zio.test

import zio.{IO, Managed, UIO, ZIO}
import zio.stream._
import zio.console._

object Zio1 extends ZioSupport {

  def main(args: Array[String]): Unit = {
//    val s1 = Managed.make(ZIO.succeed(Stream(1, 2, 3)))(_ => UIO(println("running finalizer")))
//    val s2 = s1.flatMap(_.broadcast(2, 5))
//    val s3 = s2.map(_.zipWithIndex.map(pair => pair._1.foreach(i => putStrLn(s"from ${pair._2} : $i"))))
//    val s4 = s3.flatMap(ZIO.collectAll(_).toManaged_)
//    val s5 = s4.use { res =>
//      putStrLn(res.toString())
//    }
//    println(runtime.unsafeRun(s5))

    val ss1 = Stream(1, 2, 3)
    val ss2 = ss1.mapM(i => ZIO(println(s"finalizing... $i")).as(i))
    val ss3 = ss2.foreach(i => ZIO(println(s"consuming... $i")))
    println(runtime.unsafeRun(ss3))
  }
}
