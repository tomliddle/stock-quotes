import java.time.Clock

import com.google.inject.{AbstractModule, Provides}
import models.entities.Stock
import models.persistence.AbstractBaseDAO
import models.persistence.SlickTables.StocksTable
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import org.specs2.execute.Results
import org.specs2.matcher.Matchers
import org.specs2.mock.Mockito
import play.api.Application
import play.api.libs.json.{JsObject, JsString}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationSpec extends PlaySpecification with Results with Matchers with Mockito{
  sequential

  val daoMock: AbstractBaseDAO[StocksTable, Stock] = mock[AbstractBaseDAO[StocksTable,Stock]]

  val application: Application = new GuiceApplicationBuilder().overrides(new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    }
    @Provides
    def provideSuppliersDAO : AbstractBaseDAO[StocksTable,Stock] = daoMock
  }).build

  "Routes" should {

    "send 404 on a bad request" in  {
      route(application, FakeRequest(GET, "/boum")).map(status) shouldEqual Some(NOT_FOUND)
    }

    "send 204 when there isn't a /stock/1" in  {
      daoMock.findById("1").returns(Future{None})
      route(application, FakeRequest(GET, "/stock/1")).map(status) shouldEqual Some(NO_CONTENT)
    }

    "send 200 when there is a /supplier/1" in  {
      daoMock.findById("1").returns(Future{ Some(Stock("1","name","desc")) })
      route(application, FakeRequest(GET, "/stock/1")).map(status) shouldEqual Some(OK)
    }

    "send 415 when post to create a supplier without json type" in {
      route(application, FakeRequest(PUT, "/stock")).map(status) shouldEqual Some(UNSUPPORTED_MEDIA_TYPE)
    }

    "send 400 when post to create a supplier with empty json" in {
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),JsObject(Seq()))).map(status) shouldEqual Some(BAD_REQUEST)
    }

    "send 400 when post to create a supplier with wrong json" in {
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),JsObject(Seq("wrong" -> JsString("wrong"))))).map(status) shouldEqual Some(BAD_REQUEST)
    }

    "send 201 when post to create a supplier with valid json" in {
      val (name,desc) = ("Apple","Shut up and take my money")
      daoMock.insert(Stock("0", name, desc)).returns(Future{"1"})
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),
          JsObject(Seq("name" -> JsString(name),"desc" -> JsString(desc))))).map(
        status) shouldEqual Some(CREATED)
    }

    "send 500 when post to create a supplier with valid json" in {
      val (name,desc) = ("Apple","Shut up and take my money")
      daoMock.insert(Stock("0", name, desc)).returns(Future.failed{new Exception ("Slick exception")})
      route(application,
        FakeRequest(PUT, "/stock", FakeHeaders(("Content-type","application/json") :: Nil),
          JsObject(Seq("name" -> JsString(name),"desc" -> JsString(desc))))).map(
        status) shouldEqual Some(INTERNAL_SERVER_ERROR)
    }


  }

}

