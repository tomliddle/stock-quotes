import java.time.Clock

import com.google.inject.{AbstractModule, Provides}
import models.entities.Stock
import models.persistence.AbstractBaseDAO
import models.persistence.SlickTables.StocksTable
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.Application
import play.api.libs.json.{JsObject, JsString}
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._


class ApplicationSpec extends PlaySpec with MustMatchers with MockitoSugar {

  val daoMock: AbstractBaseDAO[StocksTable, Stock] = mock[AbstractBaseDAO[StocksTable,Stock]]

  val application: Application = new GuiceApplicationBuilder().overrides(new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    }
    @Provides
    def provideStockDAO : AbstractBaseDAO[StocksTable,Stock] = daoMock
  }).build

  "Routes" should {

    "send 404 on a bad request" in  {
      route(application, FakeRequest(GET, "/boum")).map(status) must equal(Some(NOT_FOUND))
    }

    "send 204 when there isn't a /stock/1" in  {
      when(daoMock.findById("1")).thenReturn(Future{None})
      route(application, FakeRequest(GET, "/stock/1")).map(status) must equal(Some(NO_CONTENT))
    }

    "send 200 when there is a /supplier/1" in  {
      when(daoMock.findById("1")).thenReturn(Future{ Some(Stock("1","name","desc")) })
      route(application, FakeRequest(GET, "/stock/1")).map(status) must equal(Some(OK))
    }

    "send 415 when post to create a supplier without json type" in {
      route(application, FakeRequest(PUT, "/stock")).map(status) must equal(Some(UNSUPPORTED_MEDIA_TYPE))
    }

    "send 400 when post to create a supplier with empty json" in {
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),JsObject(Seq()))).map(status) must equal(Some(BAD_REQUEST))
    }

    "send 400 when post to create a supplier with wrong json" in {
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),JsObject(Seq("wrong" -> JsString("wrong"))))).map(status) must equal(Some(BAD_REQUEST))
    }

    "send 201 when post to create a supplier with valid json" in {
      val (name,desc) = ("Apple","Shut up and take my money")
      when(daoMock.insert(Stock("0", name, desc))).thenReturn((Future{"1"}))
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),
          JsObject(Seq("name" -> JsString(name),"desc" -> JsString(desc))))).map(
        status) must equal(Some(CREATED))
    }

    "send 500 when post to create a supplier with valid json" in {
      val (name,desc) = ("Apple","Shut up and take my money")
      when(daoMock.insert(Stock("0", name, desc))).thenReturn((Future.failed{new Exception ("Slick exception")}))
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),
          JsObject(Seq("name" -> JsString(name),"desc" -> JsString(desc))))).map(
        status) must equal(Some(INTERNAL_SERVER_ERROR))
    }
  }
}

