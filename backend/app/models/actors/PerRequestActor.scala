package models.actors

import javax.inject.Inject
import akka.actor.Actor
import com.google.inject.assistedinject.Assisted
import models.actors.PerRequestActor.{Test, TestReply}
import play.api.Application
import scala.concurrent.ExecutionContext

/**
  * Required for DI of an actor per request
  */
trait PerRequestActorFactory {
  def apply(tickers: Seq[String]): Actor
}

object PerRequestActor {
  final val Name: String = "perrequest-actor"

  case class Test()
  case class TestReply(overrides: Seq[String])

}

/**
  * Created per request
  * @param tickers
  */
class PerRequestActor @Inject()(@Assisted tickers: Seq[String]) (implicit val ec: ExecutionContext, implicit val app: Application)
  extends Actor {

  def receive: Receive = {
    case Test() =>
      sender ! TestReply(tickers)

  }
}
