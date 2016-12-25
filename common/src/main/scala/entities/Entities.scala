package entities

import java.time.OffsetDateTime

case class Stock(id: String, name: String)


object Protocol {

  sealed trait Message

  case class Quote(ticker: String, price: Double, datetime: OffsetDateTime) extends Message

}