package me.socure.themelio.core

trait ServiceBase[-In, Err, Out, Res[_, _]] extends (In => Res[Err, Out])
