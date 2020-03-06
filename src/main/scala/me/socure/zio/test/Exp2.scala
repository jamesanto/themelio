package me.socure.zio.test

import java.nio.ByteBuffer

object Exp2 {

  private def toBinStr(b: Byte): String = {
    String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')
  }

  private def toBinStr(ba: Array[Byte]): String = {
    ba.foldLeft("")(_ + toBinStr(_))
  }

  private def toBytes(num: Long): Array[Byte] = {
    val bb = ByteBuffer.allocate(java.lang.Long.BYTES)
    bb.putLong(num)
    bb.array()
  }

  private def toBytes(s: String): Array[Byte] = {
    s.toSeq.grouped(8).foldLeft(Seq.empty[Byte]) { (res, current) =>
      res :+ java.lang.Byte.parseByte(current.toString(), 2)
    }.toArray
  }

  private def toLong(bytes: Array[Byte]): Long = {
    val bb = ByteBuffer.allocate(java.lang.Long.BYTES)
    bb.put(bytes)
    bb.flip()
    bb.getLong
  }

  def main(args: Array[String]): Unit = {
    val num = 10L
    val bytes = toBytes(num)
    val binStr = toBinStr(bytes)
    println(binStr)
    println(toLong(toBytes(binStr)))
  }
}
