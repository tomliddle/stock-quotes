package models.entities


trait BaseEntity {
  val id : String
  def isValid = true
}

case class Stock(id: String, name: String) extends BaseEntity

case class Quote(id: String, price: Double) extends BaseEntity

// From google api
// [ {
// "id": "9594881" ,"t" : "GSK" ,"e" : "LON" ,"l" : "1,526.50" ,"l_fix" : "1526.50" ,"l_cur" : "GBX1,526.50" ,"s": "0" ,"ltt":"1:18PM GMT" ,"lt" : "Dec 19, 1:18PM GMT" ,"lt_dts" : "2016-12-19T13:18:22Z" ,"c" : "+0.50" ,"c_fix" : "0.50" ,"cp" : "0.03" ,"cp_fix" : "0.03" ,"ccol" : "chg" ,"pcls_fix" : "1526" } ]
case class GoogleQuote(t: String, e: String, l: String) {
  def toQuote: Quote = Quote(t, 2.2)
}
