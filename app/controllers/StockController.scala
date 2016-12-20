package controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import akka.actor.{ActorRef, ActorSystem, Cancellable, Props}
import models.QuoteActor
import models.QuoteActor.UpdateData
import models.entities.{Quote, Stock}
import models.persistence.{AbstractBaseDAO, BaseDAO, SlickTables}
import play.api.libs.json._
import play.api.mvc._
import models.util.JsonConverters._
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Application, Logger}
import play.api.libs.ws.WSClient
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class StockController @Inject()(
                                 app: Application,
                                 dbConfigProvider: DatabaseConfigProvider,
                                 ws: WSClient,
                                 system: ActorSystem)
                               (implicit ec: ExecutionContext) extends Controller with SlickTables {


  private implicit val application = app
  private val logger = Logger(getClass)

  val x = new SlickTables(dbConfigProvider)
  val stockDAO = x.stockTableQ
  val quoteDAO = x.quoteTableQ

  private val quoteActor: ActorRef = system.actorOf(Props(new QuoteActor(stockDAO, quoteDAO, ws)))
  private val timer: Cancellable = system.scheduler.schedule(10 seconds, 1 hour, quoteActor, UpdateData())

  def stock(id : String): Action[AnyContent] = Action.async {
    stockDAO.findById(id).map(x => x.fold(NoContent)(res => Ok(Json.toJson(res))))
  }

  def insertStock(id: String): Action[JsValue] = Action.async(parse.json) {
    request =>
      Json.fromJson[Stock](request.body) match {
        case JsSuccess(s: Stock, path: JsPath) =>
          stockDAO.insert(s).map { n => Created("Id of Stock Added : " + n) }.recoverWith {
            case e => Future.successful { InternalServerError("There was an error at the server") }
          }

        case e: JsError => Future.successful(BadRequest)
      }
  }

  def deleteStock(id: String): Action[JsValue] = Action.async(parse.json) {
    request => {
      stockDAO.deleteById(id).map {
        case 1 => Ok("")
        case 0 => NotFound("")
        case _ => Ok("more than 1 found")
      }
    }
  }
}
