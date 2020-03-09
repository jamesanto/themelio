package me.socure.themelio.core

import scala.language.higherKinds

trait ServiceBase[-In, Err, Out, Res[_, _]] extends (In => Res[Err, Out])
