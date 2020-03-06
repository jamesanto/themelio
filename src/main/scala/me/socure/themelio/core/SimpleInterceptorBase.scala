package me.socure.themelio.core

trait SimpleInterceptorBase[In, Err, Out, Res[_, _]] extends ((In, Service[In, Err, Out, Res]) => Res[Err, Out])
