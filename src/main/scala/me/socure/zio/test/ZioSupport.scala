package me.socure.zio.test

import java.text.SimpleDateFormat
import java.util.Date

import zio.stream.ZStream
import zio.{DefaultRuntime, ZIO, ZManaged}

trait ZioSupport {
  val runtime: DefaultRuntime = new DefaultRuntime {}

  private val df = new SimpleDateFormat("HH:mm:ss.SSS")

  def log[A](msg: => A): Unit = {
    println(s"[${System.currentTimeMillis()}] [${df.format(new Date())}] [$msg]")
  }

  def out[E, A](op: ZIO[Any, E, A]): Unit = {
    runtime.unsafeRun(op)
    log("---------")
  }

  def out[E, A](op: ZManaged[Any, E, A]): Unit = {
    runtime.unsafeRun(op.use { value =>
      ZIO.effect(log(value))
    })
    log("---------")
  }

  def out[E, A](op: ZStream[Any, E, A]): Unit = {
    runtime.unsafeRun(op.foreach(v => ZIO(log(v))))
    log("---------")
  }

  def out[IE, SE, A](op: ZIO[Any, IE, ZStream[Any, SE, A]])(implicit di: DummyImplicit): Unit = {
    out(op.flatMap(_.foreach(v => {
      ZIO.effect(log(v))
    })))
    log("---------")
  }

  def out[IE, SE, A](op: ZManaged[Any, IE, ZStream[Any, SE, A]])(implicit di: DummyImplicit): Unit = {
    out(op.flatMap(s => ZManaged.fromEffect(s.foreach(v => {
      ZIO.effect(log(v))
    }))))
    log("---------")
  }
}

object ZioSupport extends ZioSupport
