package models.util

import models.entities.{Quote, Stock}
import play.api.libs.json.{JsObject, Json, OFormat, Writes}

/**
  */
object JsonConverters {

  implicit val stockJ: OFormat[Stock] = Json.format[Stock]

  implicit val quoteJ: OFormat[Quote] = Json.format[Quote]


}
