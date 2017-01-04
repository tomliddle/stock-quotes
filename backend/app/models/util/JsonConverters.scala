package models.util

import entities.{FrontEndQuote, Quote, Stock}
import models.entities.GoogleQuote
import play.api.libs.json._

/**
  */
object JsonConverters {

  implicit val stockJ: OFormat[Stock] = Json.format[Stock]

  implicit val quoteJ: OFormat[Quote] = Json.format[Quote]

  implicit val googleQuoteJ: OFormat[GoogleQuote] = Json.format[GoogleQuote]

  implicit val frontendQuoteK: OFormat[FrontEndQuote] = Json.format[FrontEndQuote]
}
