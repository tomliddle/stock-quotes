package example.akkawschat

import entities.Protocol
import org.scalajs.dom.raw._
import scala.scalajs.js
import org.scalajs.dom
import upickle.default._


object Frontend extends js.JSApp {

  def main(): Unit = {

    val playground = dom.document.getElementById("playground")
    playground.innerHTML = s"Trying to join.."
    val chat = new WebSocket(getWebsocketUri(dom.document))
    chat.onopen = { (event: Event) ⇒
      playground.insertBefore(p("Chat connection was successful!"), playground.firstChild)
    }
    chat.onerror = { (event: ErrorEvent) ⇒
      playground.insertBefore(p(s"Failed: code: ${event.colno}"), playground.firstChild)
    }
    chat.onmessage = { (event: MessageEvent) ⇒
      val wsMsg = read[Protocol.Message](event.data.toString)

      wsMsg match {
        case Protocol.Quote(ticker, price, dateTime) ⇒ writeToArea(s"$price said: $ticker $dateTime")
        case _ =>
      }
    }
    chat.onclose = { (event: Event) ⇒
      dom.alert("closed")
    }

    def writeToArea(text: String): Unit =
      playground.insertBefore(p(text), playground.firstChild)
  }

  def getWebsocketUri(document: Document): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}/update"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }
}