package me.socure.themelio.core

import scala.language.higherKinds

trait SimpleInterceptorBase[In, Err, Out, Res[+_, +_]] extends ((In, In => Res[Err, Out]) => Res[Err, Out])
