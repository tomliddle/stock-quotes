package models.persistence

import models.entities.{Quote, Stock}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id: Rep[String] = column[String]("id", O.PrimaryKey)
  }

  /**
    * Stocks table
    * @param tag
    */
  class StocksTable(tag: Tag) extends BaseTable[Stock](tag, "stocks") {
    def name: Rep[String] = column[String]("name")
    def desc: Rep[String] = column[String]("desc")
    def * : ProvenShape[Stock] = (id, name, desc) <> (Stock.tupled, Stock.unapply)
  }

  implicit val stockTableQ : TableQuery[StocksTable] = TableQuery[StocksTable]


  /**
    * Quotes table
    * @param tag
    */
  class QuotesTable(tag: Tag) extends BaseTable[Quote](tag, "quotes") {
    def price: Rep[Double] = column[Double]("price")
    def * : ProvenShape[Quote] = (id, price) <> (Quote.tupled, Quote.unapply)
  }

  implicit val quoteTableQ : TableQuery[QuotesTable] = TableQuery[QuotesTable]

}
