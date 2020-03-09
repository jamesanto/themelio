package me.socure.themelio.core

import me.socure.themelio.util.TrampolineExecution

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

final case class Result[+Err, +Out](register: (Either[Err, Out] => Unit) => Unit) {
  self =>

  def map[Out1](f: Out => Out1): Result[Err, Out1] = Result[Err, Out1] { callback =>
    self.register {
      case Left(err) => callback(Left(err))
      case Right(out) => callback(Right(f(out)))
    }
  }

  def flatMap[Out1, Err1 >: Err](f: Out => Result[Err1, Out1]): Result[Err1, Out1] = Result[Err1, Out1] { callback =>
    self.register {
      case Left(err) => callback(Left(err))
      case Right(out) => f(out).register(callback)
    }
  }

  def mapFailure[Err1](f: Err => Err1): Result[Err1, Out] = Result[Err1, Out] { callback =>
    self.register {
      case Left(err) => callback(Left(f(err)))
      case Right(out) => callback(Right(out))
    }
  }

  def flatMapFailure[Err1, Out1 >: Out](f: Err => Result[Err1, Out1]): Result[Err1, Out1] = Result[Err1, Out1] { callback =>
    self.register {
      case Left(err) => f(err).register(callback)
      case Right(out) => callback(Right(out))
    }
  }

  def runUnsafe(): Future[Either[Err, Out]] = {
    val promise = Promise[Either[Err, Out]]()
    self.register { res =>
      promise.trySuccess(res)
    }
    promise.future
  }

  def runUnsafeSync(timeout: Duration = Duration.Inf): Either[Err, Out] = {
    Await.result(runUnsafe(), timeout)
  }
}

object Result {

  def async[Err, Out](register: (Either[Err, Out] => Unit) => Unit): Result[Err, Out] = Result(register)

  def sync[Err, Out](res: => Either[Err, Out]): Result[Err, Out] = async { callback =>
    callback(res)
  }

  def syncEager[Err, Out](res: Either[Err, Out]): Result[Err, Out] = sync(res)

  def success[Out](out: => Out): Result[Nothing, Out] = async { callback =>
    callback(Right(out))
  }

  def successEager[Out](out: Out): Result[Nothing, Out] = success(out)

  def failure[Err](err: => Err): Result[Err, Nothing] = async { callback =>
    callback(Left(err))
  }

  def failureEager[Err](err: Err): Result[Err, Nothing] = failure(err)

  def asyncSuccess[Out](_register: (Out => Unit) => Unit): Result[Nothing, Out] = async { callback =>
    _register { result =>
      callback(Right(result))
    }
  }

  def asyncFailure[Err](_register: (Err => Unit) => Unit): Result[Err, Nothing] = async { callback =>
    _register { result =>
      callback(Left(result))
    }
  }

  def fromFuture0[Out](f: Future[Out])(implicit ec: ExecutionContext): Result[Throwable, Out] = async { callback =>
    f.onComplete {
      case Success(value) => callback(Right(value))
      case Failure(ex) => callback(Left(ex))
    }
  }

  def fromFuture[Out](f: Future[Out]): Result[Throwable, Out] = fromFuture0(f)(TrampolineExecution.executor)

  def fromEither[Err, Out](res: => Either[Err, Out]): Result[Err, Out] = sync(res)

  def fromEitherEager[Err, Out](res: Either[Err, Out]): Result[Err, Out] = syncEager(res)

  def fromOption[Out](res: => Option[Out]): Result[Unit, Out] = async { callback =>
    res match {
      case Some(r) => callback(Right(r))
      case None => callback(Left())
    }
  }

  def fromOptionEager[Err, Out](res: Option[Out]): Result[Unit, Out] = fromOption(res)

  def fromTry[Out](res: => Try[Out])(implicit di: DummyImplicit): Result[Throwable, Out] = async { callback =>
    res match {
      case Success(out) => callback(Right(out))
      case Failure(err) => callback(Left(err))
    }
  }

  def fromTry[Out](res: => Out): Result[Throwable, Out] = async { callback =>
    Try(res) match {
      case Success(out) => callback(Right(out))
      case Failure(err) => callback(Left(err))
    }
  }

  def fromTryEager[Out](res: Try[Out]): Result[Throwable, Out] = fromTry(res)

  def fromFunction[Err, Out](f: () => Either[Err, Out]): Result[Err, Out] = sync(f())

  def fromFunction[Out](f: () => Out)(implicit di: DummyImplicit): Result[Throwable, Out] = fromTry(f())

  def fromFunctionSuccess[Out](f: () => Out): Result[Nothing, Out] = sync(Right(f()))

  def fromFunctionFailure[Err](f: () => Err): Result[Err, Nothing] = sync(Left(f()))

  def fromFunctionOption[Out](f: () => Option[Out]): Result[Unit, Out] = fromOption(f())

  def fromFunctionTry[Out](f: () => Try[Out]): Result[Throwable, Out] = fromTry(f())
}

