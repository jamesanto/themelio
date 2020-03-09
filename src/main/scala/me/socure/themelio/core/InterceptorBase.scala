package me.socure.themelio.core

import scala.language.higherKinds

trait InterceptorBase[-FIn, SIn, SErr, SOut, FErr, FOut, Res[+_, +_]] extends ((FIn, Service[SIn, SErr, SOut, Res]) => Res[FErr, FOut])
