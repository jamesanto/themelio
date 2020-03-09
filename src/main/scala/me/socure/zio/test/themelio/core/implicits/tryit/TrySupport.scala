//package me.socure.zio.test.themelio.core.implicits.tryit
//
//import me.socure.zio.test.themelio.core.{FlatMap, Map}
//
//import scala.util.Try
//
//trait TrySupport {
//  implicit def flatMapTry[Out]: FlatMap[Try, Out] = new FlatMap[Try, Out] {
//    override def flatMap[Out1](result: Try[Out], f: Out => Try[Out1]): Try[Out1] = {
//      result.flatMap(f)
//    }
//  }
//
//  implicit def mapSuccessTry[Out]: Map[Try, Out] = new Map[Try, Out] {
//    override def map[Out2](result: Try[Out], f: Out => Out2): Try[Out2] = result.map(f)
//  }
//}
//
//object TrySupport extends TrySupport
