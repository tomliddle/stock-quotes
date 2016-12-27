package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import entities.{FrontEndQuote, Quote}
import models.actors.StockUpdateActor
import models.persistence.QuotePersistence
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, WebSocket}

import scala.concurrent.ExecutionContext
import play.api.mvc.WebSocket.{FrameFormatter, MessageFlowTransformer}

/**
  * Created by tliddle on 23/12/2016.
  */
class SocketController @Inject()(quoteDAO: QuotePersistence, implicit val system: ActorSystem, implicit val materializer: Materializer)
                       (implicit ec: ExecutionContext) extends Controller {

  import models.util.JsonConverters._
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[String, FrontEndQuote]

  def index = Action { request =>
    Ok(views.html.index.render())
  }

  def socket: WebSocket = WebSocket.accept[String, FrontEndQuote] { request =>
    ActorFlow.actorRef(out => StockUpdateActor.props(out, quoteDAO))
  }
}
