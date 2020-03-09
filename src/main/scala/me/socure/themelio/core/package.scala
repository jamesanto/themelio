package me.socure.themelio

import me.socure.themelio.core.Generalizer.ImplicitsSimple

import scala.annotation.tailrec
import scala.language.higherKinds

package object core extends ImplicitsSimple {

  import Generalizer.Implicits._

  type Service[-In, Err, Out, Res[+_, +_]] = In => Res[Err, Out]
  type Interceptor[-FIn, SIn, SErr, SOut, FErr, FOut, Res[+_, +_]] = (FIn, SIn => Res[SErr, SOut]) => Res[FErr, FOut]
  type SimpleInterceptor[In, Err, Out, Res[+_, +_]] = (In, In => Res[Err, Out]) => Res[Err, Out]

  object Service {
    def apply[In, Err, Out, Res[+_, +_]](f: In => Res[Err, Out]): Service[In, Err, Out, Res] = f
  }

  object Interceptor {
    def apply[FIn, SIn, SErr, SOut, FErr, FOut, Res[+_, +_]](f: (FIn, Service[SIn, SErr, SOut, Res]) => Res[FErr, FOut]): Interceptor[FIn, SIn, SErr, SOut, FErr, FOut, Res] = f
  }

  object SimpleInterceptor {
    def apply[In, Err, Out, Res[+_, +_]](f: (In, Service[In, Err, Out, Res]) => Res[Err, Out]): SimpleInterceptor[In, Err, Out, Res] = f
  }

  implicit class RichRes[Res[+_, +_], Err, Out](val self: Res[Err, Out]) extends AnyVal {
    def map[Out1](f: Out => Out1)(implicit ms: Map[Res]): Res[Err, Out1] = ms.map(self, f)

    def mapFailure[Err1](f: Err => Err1)(implicit mf: MapFailure[Res]): Res[Err1, Out] = mf.mapFailure(self, f)

    def flatMap[Out1](f: Out => Res[Err, Out1])(implicit fm: FlatMap[Res]): Res[Err, Out1] = fm.flatMap(self, f)

    def flatMapFailure[Err1](f: Err => Res[Err1, Out])(implicit fmf: FlatMapFailure[Res]): Res[Err1, Out] = fmf.flatMapFailure(self, f)
  }

  implicit class RichService[In, Err, Out, Res[+_, +_]](val self: In => Res[Err, Out]) extends AnyVal {
    def map[Out1](f: Out => Out1)(implicit ms: Map[Res]): In => Res[Err, Out1] = {
      in: In => {
        val res = self(in)
        ms.map(res, f)
      }
    }

    def mapFailure[Err1](f: Err => Err1)(implicit mf: MapFailure[Res]): In => Res[Err1, Out] = {
      in: In => {
        val res = self(in)
        mf.mapFailure(res, f)
      }
    }

    def mapInput[In1](f: In1 => In): In1 => Res[Err, Out] = {
      in1: In1 => self(f(in1))
    }

    def interceptWith[FIn, FErr, FOut](interceptor: (FIn, In => Res[Err, Out]) => Res[FErr, FOut]): FIn => Res[FErr, FOut] = {
      fIn: FIn => interceptor(fIn, self)
    }

    def simpleInterceptWith(interceptors: Seq[(In, In => Res[Err, Out]) => Res[Err, Out]]): In => Res[Err, Out] = {
      @tailrec
      def simpleInterceptWith0(remaining: Seq[(In, In => Res[Err, Out]) => Res[Err, Out]], interceptedService: In => Res[Err, Out]): In => Res[Err, Out] = {
        remaining.headOption match {
          case None => interceptedService
          case Some(interceptor) =>
            simpleInterceptWith0(
              remaining = remaining.tail,
              interceptedService = (in: In) => interceptor(in, interceptedService)
            )
        }
      }

      simpleInterceptWith0(remaining = interceptors, interceptedService = self)
    }

    def simpleInterceptWith(first: (In, In => Res[Err, Out]) => Res[Err, Out], rest: ((In, In => Res[Err, Out]) => Res[Err, Out])*): In => Res[Err, Out] = simpleInterceptWith(Seq(first) ++ rest)

    def andThen0[Err1, Out1](next: Res[Err, Out] => Res[Err1, Out1]): In => Res[Err1, Out1] = {
      in: In => {
        val res = self(in)
        next(res)
      }
    }

    def andThen[Err1, ErrOut, Out1](next: Out => Res[Err1, Out1])(
      implicit fms: FlatMap[Res],
      generalizer: Generalizer[Err, Err1, ErrOut],
      mapFailure: MapFailure[Res]
    ): In => Res[ErrOut, Out1] = {
      in: In => {
        val res1 = self(in).mapFailure(generalizer.generalize1)
        val modifiedNext = next.andThen(_.mapFailure(generalizer.generalize2))
        fms.flatMap(res1, modifiedNext)
      }
    }

    def andThenEither[Err1, Out1](next: Out => Res[Err1, Out1])(
      implicit fms: FlatMap[Res],
      mapFailure: MapFailure[Res]
    ): In => Res[Either[Err, Err1], Out1] = andThen(next)

    def flatMap[Out1](next: Out => Res[Err, Out1])(implicit fms: FlatMap[Res]): In => Res[Err, Out1] = {
      in: In => {
        val res = self(in)
        fms.flatMap(res, next)
      }
    }

    def andThenFailure[Err1](next: Err => Res[Err1, Out])(implicit fmf: FlatMapFailure[Res]): In => Res[Err1, Out] = {
      in: In => {
        val res = self(in)
        fmf.flatMapFailure(res, next)
      }
    }
  }

//  implicit class RichInterceptor[-FIn, SIn, SErr, SOut, FErr, FOut, Res[+_, +_]](val self: (FIn, SIn => Res[SErr, SOut]) => Res[FErr, FOut]) extends AnyVal {
//    def withPrevious[FIn1, FErr1, FOut1](previous: (FIn1, FIn => Res[FErr, FOut]) => Res[FErr1, FOut1]): (FIn1, FIn => Res[FErr, FOut]) => Res[FErr1, FOut1] = {
//      (input: FIn1, service: FIn => Res[FErr, FOut]) => {
//
//      }
//    }
//  }

}
