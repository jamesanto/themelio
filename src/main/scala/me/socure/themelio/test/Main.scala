package me.socure.themelio.test

object Main {

  import me.socure.themelio.core._
  import me.socure.themelio.implicits._

  def doubleEven(i: Int): Either[String, Double] = {
    if (i % 2 == 0) Right(i * i) else Left("Expecting an even number")
  }

  def interceptEven[Res[_, _] : MapSuccess : MapFailure](input: MyCustomInputI, service: Service[MyCustomInputS, MyCustomErrS, MyCustomOutS, Res]): Res[MyCustomErrI, MyCustomOutI] = {
    val res = service(MyCustomInputS(input.value))
    res
      .mapSuccess(out => MyCustomOutI(out.value))
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
    val finalService = service
      .mapSuccess(MyCustomOutS)
      .mapFailure(MyCustomErrS)
      .mapInput[MyCustomInputS](_.value)
      .interceptWith(interceptEven[Either])

    val res1 = finalService(MyCustomInputI(3))
    val res2 = finalService(MyCustomInputI(4))
    println(res1)
    println(res2)
  }
}
