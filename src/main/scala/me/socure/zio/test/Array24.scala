package me.socure.zio.test

class Array24 private(underlying: Array[Int]) {
  def update(hour: Int, value: Int): Unit = {
    underlying(hour - 1) = value
  }
  def query(start: Int, end: Int): Int = ???
}
