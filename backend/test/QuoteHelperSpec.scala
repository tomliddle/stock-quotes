
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import entities.Stock
import models.actors.QuoteHelper
import models.entities.Stock
import models.persistence.{QuotePersistence, StockPersistence}
import org.scalatest.MustMatchers
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import play.api.mvc.Results
import org.mockito.Mockito._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps

/**
  */
class QuoteHelperSpec extends PlaySpec with Results with MustMatchers with MockitoSugar {

  val daoStockMock: StockPersistence = mock[StockPersistence]
  val daoQuoteMock: QuotePersistence = mock[QuotePersistence]
  val wsMock: WSClient = mock[WSClient]

  implicit val system = ActorSystem("test")
  implicit val materializer = ActorMaterializer()
  val wsc = AhcWSClient()

  val qh = new QuoteHelper {
    override val ec: ExecutionContextExecutor = ExecutionContext.global
    override val logger = Logger("")
    override val stockDAO: StockPersistence = daoStockMock
    override val quoteDAO: QuotePersistence = daoQuoteMock
    override val ws: AhcWSClient = wsc
  }

  val tickers = Seq("LON:GSK", "NASDAQ:AAPL")

  "QuoteHelper" should {

    "return only listed stock" in {
      val stockList = Seq(Stock(tickers.head, "glaxo", "pharma"))

      when(daoStockMock.findById(tickers)) thenReturn Future { stockList }

      val f = qh.listedStocks(tickers)

      Await.result(f, 1 second) must equal(stockList)

    }

    "return 2 stocks" in {
      val stockList = Seq(Stock(tickers.head, "glaxo", "pharma"), Stock(tickers(1), "apple", "tech"))

      when(daoStockMock.findById(tickers)).thenReturn(Future {stockList})

      val f = qh.listedStocks(tickers)

      Await.result(f, 1 second) must equal( stockList)
    }

    "return 1 stock, one missing" in {
      val stockList = Seq(Stock(tickers.head, "glaxo", "pharma"))

      when(daoStockMock.findById(tickers)).thenReturn(Future { stockList })

      val f = qh.listedStocks(tickers)

      Await.result(f, 1 second) must equal( stockList)
    }

    "return all stocks" in {
      val stockList = Seq(Stock(tickers.head, "glaxo", "pharma"), Stock(tickers(1), "apple", "tech"))

      when(daoStockMock.findAll).thenReturn(Future { stockList})

      val f = qh.listedStocks(Seq())

      Await.result(f, 1 second) must equal( stockList)
    }

    "return no stocks" in {
      when(daoStockMock.findAll).thenReturn(Future { Seq() })

      val f = qh.listedStocks(Seq())

      Await.result(f, 1 second) must equal( Seq())
    }

    // Testing the actual request to google finance
    "update 2 stocks calling google" in {
      val stockList = Seq(Stock(tickers.head, "glaxo", "pharma"), Stock(tickers(1), "apple", "tech"))

      when(daoStockMock.findAll).thenReturn(Future { stockList })

     // daoQuoteMock.insert()

      qh.updateStocks()

      //Thread.sleep(5000)

      //daoQuoteMock expects
     // Mockito.verify(daoQuoteMock, times(2)).insert("was called at least twice")

      // Allows us to match against a generic class
     // import scala.reflect.classTag
     // val c = classTag[Quote]
   //   val a = any(c)
//
     // there were two(daoQuoteMock).insert(argThat((q: Quote) => q.id == "" && q.price > 0))

      import org.mockito.Mockito._
      //val x: Nothing = argThat((q: Quote) => true)
      //verify(daoQuoteMock).insert(Quote(tickers.head, anyDouble))
   //   verify(daoQuoteMock).insert(Quote(tickers.last, anyDouble))
      //there was times

    }
  }
}
