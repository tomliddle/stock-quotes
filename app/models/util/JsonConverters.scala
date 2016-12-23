package models.util

import models.entities.{GoogleQuote, Quote, Stock}
import play.api.libs.json._

/**
  */
object JsonConverters {

  implicit val stockJ: OFormat[Stock] = Json.format[Stock]

  implicit val quoteJ: OFormat[Quote] = Json.format[Quote]

  implicit val googleQuoteJ: OFormat[GoogleQuote] = Json.format[GoogleQuote]

}
