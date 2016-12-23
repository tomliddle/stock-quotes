package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.actors.PerRequestActorFactory
import models.persistence.QuotePersistence
import play.api.libs.concurrent.InjectedActorSupport
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class PerRequestController @Inject()(quoteDAO: QuotePersistence, implicit val system: ActorSystem, praFactory: PerRequestActorFactory)
                                  (implicit ec: ExecutionContext) extends Controller with InjectedActorSupport {

    var counter = 0

  def perRequestActor =  Action { request =>
    //  val a: ActorRef = injectedChild(praFactory(Seq("AAPL")), s"actor-socket-$counter")
    counter += 1
    Ok("")
  }

}
