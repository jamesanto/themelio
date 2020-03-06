package me.socure.zio.test.themelio

import scala.language.higherKinds


package object core {

  trait FlatMap[R[_], Out] {
    def flatMap[Out1](result: R[Out], f: Out => R[Out1]): R[Out1]
  }

  trait Map[R[_], Out] {
    def map[Out1](result: R[Out], f: Out => Out1): R[Out1]
  }

  trait Service[-In, Out, R[_]] extends (In => R[Out])

  class FuncService[-In, Out, R[_]](f: In => R[Out]) extends Service[In, Out, R] {
    override def apply(in: In): R[Out] = f(in)
  }

  object Service {
    def apply[In, Out, R[_]](f: In => R[Out]): Service[In, Out, R] = new FuncService(f)
  }

  trait Interceptor0[-FIn, +SIn, SOut, FOut, R[_]] extends ((FIn, Service[SIn, SOut, R]) => R[FOut])

  class FuncInterceptor0[-FIn, +SIn, SOut, FOut, R[_]](f: (FIn, Service[SIn, SOut, R]) => R[FOut]) extends Interceptor0[FIn, SIn, SOut, FOut, R] {
    override def apply(input: FIn, service: Service[SIn, SOut, R]): R[FOut] = f(input, service)
  }

  object Interceptor0 {
    def apply[FIn, SIn, SOut, FOut, R[_]](f: (FIn, Service[SIn, SOut, R]) => R[FOut]): Interceptor0[FIn, SIn, SOut, FOut, R] = new FuncInterceptor0(f)
  }

  trait Interceptor[SIn, SOut, R[_]] extends Interceptor0[SIn, SIn, SOut, SOut, R]

  class FuncInterceptor[SIn, SOut, R[_]](f: (SIn, Service[SIn, SOut, R]) => R[SOut]) extends FuncInterceptor0[SIn, SIn, SOut, SOut, R](f) with Interceptor[SIn, SOut, R]

  object Interceptor {
    def apply[SIn, SOut, R[_]](f: (SIn, Service[SIn, SOut, R]) => R[SOut]): Interceptor[SIn, SOut, R] = new FuncInterceptor[SIn, SOut, R](f)
  }

  trait ServiceFactory[-In, Out, R[_]] extends (() => Service[In, Out, R]) with Service[In, Out, R] {
    override def apply(v1: In): R[Out] = apply().apply(v1)
  }

  implicit class RichService[In, Out, R[_]](val self: Service[In, Out, R]) extends AnyVal {
    def mapRes[Out1](f: R[Out] => R[Out1]): Service[In, Out1, R] = {
      in: In => {
        val result = self(in)
        f(result)
      }
    }

    def map[Out1](f: Out => Out1)(implicit ms: Map[R, Out]): Service[In, Out1, R] = {
      in: In => {
        val result = self(in)
        ms.map[Out1](result, f)
      }
    }

    def andThen[Out1](next: Service[Out, Out1, R])(implicit fm: FlatMap[R, Out]): Service[In, Out1, R] = {
      in: In => {
        val result = self(in)
        fm.flatMap[Out1](result, out => next(out))
      }
    }

    def contramap[In1](f: In1 => In): Service[In1, Out, R] = {
      in1: In1 => self(f(in1))
    }

    def interceptWith0[FIn, FOut](interceptor: Interceptor0[FIn, In, Out, FOut, R]): Service[FIn, FOut, R] = {
      fIn: FIn => interceptor(fIn, self)
    }

    def interceptWith(interceptor: Interceptor[In, Out, R]): Service[In, Out, R] = interceptWith0[In, Out](interceptor)
  }

}
