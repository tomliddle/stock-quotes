package controllers

import javax.inject.{Inject, Named, Provider, Singleton}

import akka.actor.{ActorRef, ActorSystem, Cancellable, Props}
import models.QuoteActor
import models.QuoteActor.UpdateData
import models.persistence.{QuotePersistence, StockPersistence}
import play.api.{Application, Logger}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import models.util.JsonConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.language.postfixOps

/**
  */
@Singleton
class QuoteController @Inject()(quoteDAO: QuotePersistence, @Named("quote-actor") quoteActor: ActorRef, system: ActorSystem)
                               (implicit ec: ExecutionContext) extends Controller {


  private val logger = Logger(getClass)

  private val timer: Cancellable = system.scheduler.schedule(10 seconds, 10 seconds, quoteActor, UpdateData())

  def allQuote: Action[AnyContent] = Action.async {
    quoteDAO.findAll.map(x => Ok(Json.toJson(x)))
  }

  def quote(id : String): Action[AnyContent] = Action.async {
    quoteDAO.filterTicker(id).map(x => Ok(Json.toJson(x)))
  }


}
