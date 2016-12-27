package models.persistence

import java.sql.Timestamp
import java.time.{OffsetDateTime, ZoneOffset}

import slick.lifted.ProvenShape
import javax.inject.Inject

import entities.Quote
import entities.Stock
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}



class StockPersistence @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AbstractBaseDAO[Stock, String] {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  val stocksQ = TableQuery[StocksTable]

  override def insert(stock: Seq[Stock]): Future[Int] = {
    val q = stock.map(s => stocksQ += s)
    val q2 = DBIO.sequence(q)
    dbConfig.db.run(q2).map(_.sum)
  }

  override def save(stock: Seq[Stock]): Future[Int] = {
    val q = stock.map(s => stocksQ.insertOrUpdate(s))
    val q2 = DBIO.sequence(q)
    dbConfig.db.run(q2).map(_.sum)
  }

  override def findById(ids: Seq[String]): Future[Seq[Stock]] = dbConfig.db.run(stocksQ.filter(_.id inSetBind ids).result)

  override def findAll: Future[Seq[Stock]] = dbConfig.db.run(stocksQ.result)

  override def delete(ids : Seq[String]): Future[Int] = {
    val q = stocksQ.filter(_.id inSetBind ids)
    dbConfig.db.run(q.delete)
  }


  /**
    * Stocks table
    * @param tag
    */
  class StocksTable(tag: Tag) extends Table[Stock](tag, "stocks") {
    def id: Rep[String] = column[String]("id")
    def name: Rep[String] = column[String]("name")
    def * : ProvenShape[Stock] = (id, name) <> (Stock.tupled, Stock.unapply)
  }
}


class QuotePersistence @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AbstractBaseDAO[Quote, String] {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  private val quotesQ = TableQuery[QuotesTable]

  override def insert(stock: Seq[Quote]): Future[Int] = {
    val q = stock.map(s => quotesQ += s)
    val q2 = DBIO.sequence(q)
    dbConfig.db.run(q2).map(_.sum)
  }

  override def save(quote: Seq[Quote]): Future[Int] = {
    val q = quote.map(s => quotesQ.insertOrUpdate(s))
    val q2 = DBIO.sequence(q)
    dbConfig.db.run(q2).map(_.sum)
  }

  def filterTicker(s: String): Future[Seq[Quote]] = {
    val q = quotesQ.filter(_.ticker === s).result
    dbConfig.db.run(q)
  }

  override def findById(ids: Seq[String]): Future[Seq[Quote]] = {
    val q = quotesQ.filter(_.ticker inSetBind ids).result
    dbConfig.db.run(q)
  }
  override def findAll: Future[Seq[Quote]] = dbConfig.db.run(quotesQ.result)

  override def delete(ids : Seq[String]): Future[Int] = {
    val q = quotesQ.filter(s => s.ticker inSetBind ids)
    dbConfig.db.run(q.delete)
  }

  def latest(ticker: String): Future[Option[Quote]] = {
    val q = quotesQ.filter(_.ticker === ticker).sortBy(_.time).take(1).result
    dbConfig.db.run(q.headOption)
  }

  private implicit val JavaZonedDateTimeMapper = MappedColumnType.base[OffsetDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => OffsetDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )


  /**
    * Quotes table
    * @param tag
    */
  class QuotesTable(tag: Tag) extends Table[Quote](tag, "quotes") {
    def ticker: Rep[String] = column[String]("ticker")
    def price: Rep[BigDecimal] = column[BigDecimal]("price")
    def time: Rep[OffsetDateTime] = column[OffsetDateTime]("datetime")
    def * : ProvenShape[Quote] = (ticker, price, time) <> (Quote.tupled, Quote.unapply)
  }
}






