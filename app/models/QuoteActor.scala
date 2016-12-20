package models

import akka.actor.{Actor, Props}
import models.QuoteActor.{GetStocks, Stocks, UpdateData}
import models.entities.{GoogleQuote, Quote, Stock}
import models.persistence.{AbstractBaseDAO, SlickTables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.util.JsonConverters._
import play.api.{Application, Logger}

object QuoteActor {

  def props: Props = Props[QuoteActor]

  case class GetStocks(list: Seq[String])
  case class Stocks(list: Seq[Stock])
  case class UpdateData()
}
/**
  */
class QuoteActor(val stockDAO: AbstractBaseDAO[StocksTable,Stock], val quoteDAO: AbstractBaseDAO[QuotesTable, Quote], val ws: WSClient)
                (implicit val ec: ExecutionContext, implicit val app: Application)
  extends Actor with QuoteHelper {

  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile]

  def receive: Receive = {
    case GetStocks(list) =>
      val replyTo = sender
      listedStocks(list).map(replyTo ! Stocks(_))

    case UpdateData() =>
      updateStocks()
  }
}


trait QuoteHelper {

  protected implicit val ec: ExecutionContext
  protected val logger: Logger = Logger(getClass)
  protected val stockDAO: AbstractBaseDAO[StocksTable,Stock]
  protected val quoteDAO: AbstractBaseDAO[QuotesTable, Quote]
  protected val ws: WSClient

  def url(ticker: String) = s"https://www.google.com/finance/info?q=$ticker"

  def listedStocks(list: Seq[String]): Future[Seq[Stock]] = {
      if (list.isEmpty) stockDAO.findAll
      else stockDAO.findByFilter { q => list.contains(q.name.toString) }
  }

  def updateStocks(): Unit = {
    stockDAO.findAll.foreach { stocks =>
      stocks.foreach { (s: Stock) =>
        val f: Future[WSResponse] = ws.url(url(s.id)).get()

        f.foreach { (r: WSResponse) =>
          r.status match {
            case 200 =>
              val body = r.body.dropWhile(c => c != '[')
              val json: JsValue = Json.parse(body)
              Json.fromJson[Seq[GoogleQuote]](json) match {
                case JsSuccess(qs: Seq[GoogleQuote], path: JsPath) =>
                  qs.headOption match {
                    case Some(q) => quoteDAO.insert(q.toQuote)
                    case None => logger.error("empty list returned")
                  }
                case e: JsError => logger.error(s"cannot convert from json $e")
              }
            case _ =>
              logger.error(s"Error is ${r.statusText} ${r.status} ${r.body} ${url(s.id)}")
          }
        }
      }
    }
  }
}