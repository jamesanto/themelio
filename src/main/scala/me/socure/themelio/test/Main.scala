package me.socure.themelio.test

object Main {

  import me.socure.themelio.core._
  import me.socure.themelio.implicits._

  case class CustomDouble(value: Double) extends AnyVal

  def doubleEven(i: Int): Result[String, Double] = {
    if (i % 2 == 0) Result.success(i * i) else Result.failure("Expecting an even number")
  }

  def doubleEvenDouble(d: Double): Result[String, CustomDouble] = {
    if (d % 2 == 0) Result.success(CustomDouble(d * d)) else Result.failure("error")
  }

  def interceptEven[Res[+_, +_] : Map : MapFailure](input: MyCustomInputI, service: Service[MyCustomInputS, MyCustomErrS, MyCustomOutS, Res]): Res[MyCustomErrI, MyCustomOutI] = {
    val res = service(MyCustomInputS(input.value))
    res
      .map(out => MyCustomOutI(out.value))
      .mapFailure(err => MyCustomErrI(err.value))
  }

  case class MyCustomInputS(value: Int) extends AnyVal

  case class MyCustomErrS(value: String) extends AnyVal

  case class MyCustomOutS(value: Double) extends AnyVal

  case class MyCustomInputI(value: Int) extends AnyVal

  case class MyCustomErrI(value: String) extends AnyVal

  case class MyCustomOutI(value: Double) extends AnyVal

  def main(args: Array[String]): Unit = {
    val service = doubleEven _
    val service2 = doubleEvenDouble _
//    val service3: Service[Int, String, CustomDouble, Result] = service.andThen(service2)


    val finalService = service
      .map(MyCustomOutS)
      .mapFailure(MyCustomErrS)
      .mapInput[MyCustomInputS](_.value)
      .interceptWith(interceptEven[Result])

    val res1 = finalService(MyCustomInputI(3))
    val res2 = finalService(MyCustomInputI(4))
    println(res1.runUnsafeSync())
    println(res2.runUnsafeSync())
  }
}
