package controllers

import javax.inject.{Inject, Named, Singleton}
import akka.actor.{ActorRef, ActorSystem, Cancellable}
import models.actors.PerRequestActorFactory
import models.actors.QuoteActor.UpdateData
import models.persistence.QuotePersistence
import models.util.JsonConverters._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  */
@Singleton
class QuoteController @Inject()(quoteDAO: QuotePersistence, @Named("quote-actor") quoteActor: ActorRef, system: ActorSystem)
                               (implicit ec: ExecutionContext) extends Controller {


  private val logger = Logger(getClass)

  logger.info("adding timer")
  private val timer: Cancellable = system.scheduler.schedule(20 seconds, 10 seconds, quoteActor, UpdateData())

  def allQuote: Action[AnyContent] = Action.async {
    quoteDAO.findAll.map(x => Ok(Json.toJson(x)))
  }

  def quote(id : String): Action[AnyContent] = Action.async {
    quoteDAO.filterTicker(id).map(x => Ok(Json.toJson(x)))
  }


}
