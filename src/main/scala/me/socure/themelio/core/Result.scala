package me.socure.themelio.core

sealed abstract class Result[+Err, +Out]
final case class Sync[+Err, +Out](value: Either[Err, Out]) extends Result[Err, Out]
final case class Async[+Err, +Out]() extends Result[Err, Out]
