package models

import akka.actor.Actor
import models.QuoteActor.{GetStocks, Stocks, UpdateData}
import models.entities.{Quote, Stock}
import models.persistence.AbstractBaseDAO
import models.persistence.SlickTables.{QuotesTable, StocksTable}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}
import play.api.libs.ws.{WS, WSClient, WSResponse}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future
import models.util.JsonConverters._
import play.api.Logger

object QuoteActor {

  case class GetStocks(list: Seq[String])
  case class Stocks(list: Seq[Stock])
  case class UpdateData()
}
/**
  */
class QuoteActor(stockDAO: AbstractBaseDAO[StocksTable,Stock], quoteDAO: AbstractBaseDAO[QuotesTable, Quote], ws: WSClient) extends Actor {

  private val logger = Logger(getClass)
  private[this] def url(ticker: String) = s"http://www.google.com/finance/info?q=$ticker"

  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile]



  def receive: Receive = {
    case GetStocks(list) =>
      val stocks: Future[Seq[Stock]] =
        if (list.isEmpty) stockDAO.findAll
        else stockDAO.findByFilter { q => list.contains(q.name.toString) }

      val replyTo = sender
      stocks.map(replyTo ! Stocks(_))

    case UpdateData() =>
      stockDAO.findAll.foreach { stocks =>
        stocks.foreach { (s: Stock) =>
          val f: Future[WSResponse] = ws.url(url(s.id)).get()

          f.foreach { (r: WSResponse) =>
            Json.fromJson[Quote](r.json) match {
              case JsSuccess(q: Quote, path: JsPath) => quoteDAO.insert(q)
              case e: JsError => logger.warn(s"cannot convert from json $e")
            }
          }
        }
      }
  }
}
