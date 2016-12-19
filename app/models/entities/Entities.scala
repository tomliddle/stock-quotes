package models.entities


trait BaseEntity {
  val id : String
  def isValid = true
}

case class Stock(id: String, name: String, desc: String) extends BaseEntity

case class Quote(id: String, price: Double) extends BaseEntity
