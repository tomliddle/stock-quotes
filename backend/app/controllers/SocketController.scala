package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer
import models.actors.StockUpdateActor
import models.persistence.QuotePersistence
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, WebSocket}

import scala.concurrent.ExecutionContext

/**
  * Created by tliddle on 23/12/2016.
  */
class SocketController @Inject()(quoteDAO: QuotePersistence, implicit val system: ActorSystem, implicit val materializer: Materializer)
                       (implicit ec: ExecutionContext) extends Controller {


  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => StockUpdateActor.props(out, Seq("AAPL"), quoteDAO))
  }
}
