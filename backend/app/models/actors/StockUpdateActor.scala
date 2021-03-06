package models.actors

import akka.actor.{Actor, ActorRef, Props}
import entities.Quote
import models.persistence.QuotePersistence
import scala.concurrent.ExecutionContext

object StockUpdateActor {
  def props(out: ActorRef, quoteDAO: QuotePersistence)(implicit ec: ExecutionContext): Props = Props(new StockUpdateActor(out, quoteDAO))
}

class StockUpdateActor(out: ActorRef, quoteDAO: QuotePersistence)(implicit ec: ExecutionContext) extends Actor {

  context.system.eventStream.subscribe(self, classOf[Quote])

  def receive: Receive = {
    case msg: String =>
      quoteDAO.latest(msg).foreach(_.foreach(out ! _))

    case q: Quote =>
      out ! q.toFrontEndQuote
  }
}