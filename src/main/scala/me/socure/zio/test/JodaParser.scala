package me.socure.zio.test

import org.joda.time.LocalDate
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, ISODateTimeFormat}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class LocalDateParser(formats: Seq[DateTimeFormatter]) {
  def parse(d: String): Option[LocalDate] = {
    @tailrec
    def tryLocalDate0(remainingFormats: Seq[DateTimeFormatter]): Option[LocalDate] = {
      remainingFormats.headOption match {
        case None => None
        case Some(formatter) => Try(formatter.parseLocalDate(d)) match {
          case Success(date) => Some(date)
          case Failure(_) => tryLocalDate0(remainingFormats.tail)
        }
      }
    }

    tryLocalDate0(formats)
  }
}

object DobParser extends LocalDateParser(Seq(
  ISODateTimeFormat.date(),
  ISODateTimeFormat.dateTime(),
  DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
  DateTimeFormat.forPattern("yyyy-MM-dd"),
  DateTimeFormat.forPattern("yyyyMMdd"),
  DateTimeFormat.forPattern("yyyy/MM/dd"),
  DateTimeFormat.forPattern("MM/dd/yyyy")
))

object TestMain {
  def main(args: Array[String]): Unit = {
    println(DobParser.parse("20200305"))
    println(DobParser.parse("20200305545"))
  }
}
