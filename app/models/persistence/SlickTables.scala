package models.persistence

import models.entities.{Quote, Stock}
import models.persistence.DAO.BaseTable
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape


/**
  * The companion object.
  */
class SlickTables(dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._

  /**
    * Stocks table
    * @param tag
    */
  class StocksTable(tag: Tag) extends BaseTable[Stock](tag, "stocks", dbConfigProvider) {
    def name: Rep[String] = column[String]("name")
    def desc: Rep[String] = column[String]("desc")
    def * : ProvenShape[Stock] = (id, name, desc) <> (Stock.tupled, Stock.unapply)
  }

  implicit val stockTableQ : TableQuery[StocksTable] = TableQuery[StocksTable]


  /**
    * Quotes table
    * @param tag
    */
  class QuotesTable(tag: Tag) extends BaseTable[Quote](tag, "quotes", dbConfigProvider) {
    def price: Rep[Double] = column[Double]("price")
    def * : ProvenShape[Quote] = (id, price) <> (Quote.tupled, Quote.unapply)
  }

  implicit val quoteTableQ : TableQuery[QuotesTable] = TableQuery[QuotesTable]

//  def stockDAO = new BaseDAO[StocksTable,Stock]
//  def quoteDAO = new BaseDAO[QuotesTable,Quote]

}
