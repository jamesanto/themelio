package me.socure.themelio

package object core {

  import Generalizer.Implicits._

  type Service[-In, Err, Out, Res[_, _]] = In => Res[Err, Out]
  type Interceptor[-FIn, SIn, SErr, SOut, FErr, FOut, Res[_, _]] = (FIn, Service[SIn, SErr, SOut, Res]) => Res[FErr, FOut]
  type SimpleInterceptor[In, Err, Out, Res[_, _]] = Interceptor[In, In, Err, Out, Err, Out, Res]

  object Service {
    def apply[In, Err, Out, Res[_, _]](f: In => Res[Err, Out]): Service[In, Err, Out, Res] = f
  }

  object Interceptor {
    def apply[FIn, SIn, SErr, SOut, FErr, FOut, Res[_, _]](f: (FIn, Service[SIn, SErr, SOut, Res]) => Res[FErr, FOut]): Interceptor[FIn, SIn, SErr, SOut, FErr, FOut, Res] = f
  }

  object SimpleInterceptor {
    def apply[In, Err, Out, Res[_, _]](f: (In, Service[In, Err, Out, Res]) => Res[Err, Out]): SimpleInterceptor[In, Err, Out, Res] = f
  }

  implicit class RichRes[Res[_, _], Err, Out](val res: Res[Err, Out]) extends AnyVal {
    def mapSuccess[Out1](f: Out => Out1)(implicit ms: MapSuccess[Res]): Res[Err, Out1] = ms.mapSuccess(res, f)

    def mapFailure[Err1](f: Err => Err1)(implicit mf: MapFailure[Res]): Res[Err1, Out] = mf.mapFailure(res, f)
  }

  implicit class RichService[In, Err, Out, Res[_, _]](val self: In => Res[Err, Out]) extends AnyVal {
    def mapSuccess[Out1](f: Out => Out1)(implicit ms: MapSuccess[Res]): Service[In, Err, Out1, Res] = {
      in: In => {
        val res = self(in)
        ms.mapSuccess(res, f)
      }
    }

    def mapFailure[Err1](f: Err => Err1)(implicit mf: MapFailure[Res]): Service[In, Err1, Out, Res] = {
      in: In => {
        val res = self(in)
        mf.mapFailure(res, f)
      }
    }

    def mapInput[In1](f: In1 => In): Service[In1, Err, Out, Res] = {
      in1: In1 => self(f(in1))
    }

    def interceptWith[FIn, FErr, FOut](interceptor: Interceptor[FIn, In, Err, Out, FErr, FOut, Res]): Service[FIn, FErr, FOut, Res] = {
      fIn: FIn => interceptor(fIn, self)
    }

    def simpleInterceptWith(interceptor: SimpleInterceptor[In, Err, Out, Res]): Service[In, Err, Out, Res] = {
      in: In => interceptor(in, self)
    }

    def andThen[Err1, Out1](next: Res[Err, Out] => Res[Err1, Out1]): Service[In, Err1, Out1, Res] = {
      in: In => {
        val res = self(in)
        next(res)
      }
    }

    def andThen[Err1, ErrOut, Out1](next: Out => Res[Err1, Out1])(
      implicit fms: FlatMapSuccess[Res],
      generalizer: Generalizer[Err, Err1, ErrOut],
      mapFailure: MapFailure[Res]
    ): Service[In, ErrOut, Out1, Res] = {
      in: In => {
        val res1 = self(in).mapFailure(generalizer.generalize1)
        val modifiedNext = next.andThen(_.mapFailure(generalizer.generalize2))
        fms.flatMapSuccess(res1, modifiedNext)
      }
    }

    def andThenEither[Err1, Out1](next: Out => Res[Err1, Out1])(
      implicit fms: FlatMapSuccess[Res],
      mapFailure: MapFailure[Res]
    ): Service[In, Either[Err, Err1], Out1, Res] = andThen(next)

    def andThenSuccess[Out1](next: Out => Res[Err, Out1])(implicit fms: FlatMapSuccess[Res]): Service[In, Err, Out1, Res] = {
      in: In => {
        val res = self(in)
        fms.flatMapSuccess(res, next)
      }
    }

    def andThenFailure[Err1](next: Err => Res[Err1, Out])(implicit fmf: FlatMapFailure[Res]): Service[In, Err1, Out, Res] = {
      in: In => {
        val res = self(in)
        fmf.flatMapFailure(res, next)
      }
    }
  }

}
