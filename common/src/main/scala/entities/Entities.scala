package entities

import java.time.{LocalDateTime, OffsetDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter



case class Stock(id: String, name: String)

case class FrontEndQuote(ticker: String, price: Double, date: String) {

  override def toString: String = {
    s"Ticker: $ticker price: $price time: $date"
  }
}

case class Quote(ticker: String, price: BigDecimal, datetime: OffsetDateTime) {
   val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
   def time: String = datetime.format(dateTimeFormatter)

  def toFrontEndQuote = FrontEndQuote(ticker, price.toDouble, time)
} //extends Message

