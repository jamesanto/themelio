package me.socure.themelio.core

final case class Contextual[Ctx,Val](context: Ctx, value: Val)
