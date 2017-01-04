package models.actors

import java.time.OffsetDateTime
import javax.inject.Inject

import akka.actor.{Actor, Props}
import entities.Quote
import entities.Stock
import models.actors.QuoteActor.{GetStocks, Stocks, UpdateData}
import models.entities.GoogleQuote
import models.persistence.{QuotePersistence, StockPersistence}
import models.util.JsonConverters._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.{Application, Logger}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal.RoundingMode

object QuoteActor {

  final val Name: String = "quote-actor"

  def props: Props = Props[QuoteActor]

  case class GetStocks(list: Seq[String])
  case class Stocks(list: Seq[Stock])
  case class UpdateData(list: Seq[String] = Seq.empty)
}
/**
  */
class QuoteActor @Inject()(val stockDAO: StockPersistence, val quoteDAO: QuotePersistence, val ws: WSClient)
                (implicit val ec: ExecutionContext, implicit val app: Application)
  extends Actor with QuoteHelper{

  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile]

  def receive: Receive = {
    case GetStocks(list) =>
      val replyTo = sender
      listedStocks(list).map(replyTo ! Stocks(_))

    case UpdateData(list) =>
      updateStocks(list, publish)

  }

  private def publish(q: Quote): Unit = {
    context.system.eventStream.publish(q)
  }
}


trait QuoteHelper {

  protected implicit val ec: ExecutionContext
  protected val logger: Logger = Logger(getClass)
  protected val stockDAO: StockPersistence
  protected val quoteDAO: QuotePersistence
  protected val ws: WSClient

  def url(ticker: String) = s"https://www.google.com/finance/info?q=$ticker"

  /**
    * Get the listed stocks
    * @param list
    * @return
    */
  def listedStocks(list: Seq[String]): Future[Seq[Stock]] = {
      if (list.isEmpty) stockDAO.findAll
      else stockDAO.findById(list)
  }

  /**
    * Update the list of stocks in the database and call the callback method
    * @param list list of stocks
    * @param fPub publish method for a stock quote
    */
  def updateStocks(list: Seq[String], fPub: Quote => Unit): Unit = {
      logger.info(s"updating $list")
      listedStocks(list).foreach { stocks =>
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
                    case Some(gq) =>
                      val q = gq.toQuote(OffsetDateTime.now)
                      quoteDAO.latest(q.ticker).foreach { l =>
                        if (l.isEmpty || !q.price.equals(l.get.price.setScale(2, RoundingMode.HALF_UP)))
                          logger.info(s"adding quote $l $q")
                          fPub(q)
                          quoteDAO.insert(q)
                      }

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