package me.socure.themelio.core

trait InterceptorBase[-FIn, SIn, SErr, SOut, FErr, FOut, Res[_, _]] extends ((FIn, Service[SIn, SErr, SOut, Res]) => Res[FErr, FOut])
