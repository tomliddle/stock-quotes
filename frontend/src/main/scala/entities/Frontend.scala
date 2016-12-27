package entities


import org.scalajs.dom
import org.scalajs.dom.raw._
import scala.scalajs.js

case class NewQuote(ticker: String, price: Double, date: String)


object Frontend extends js.JSApp {

  def main(): Unit = {

    val quotesDiv = dom.document.getElementById("quotes")
    quotesDiv.innerHTML = s"Trying to join.."
    val chat = new WebSocket(getWebsocketUri(dom.document))

    chat.onopen = { (event: Event) ⇒
      quotesDiv.innerHTML = "Connection was successful!"
    }
    chat.onerror = { (event: ErrorEvent) ⇒
      quotesDiv.innerHTML = s"Failed: code: ${event.colno}"
    }
    chat.onmessage = { (event: MessageEvent) ⇒
      val quote = upickle.default.read[FrontEndQuote](event.data.toString)

      quotesDiv.innerHTML = s"received message ${quote.toString}"
    }
    chat.onclose = { (event: Event) ⇒
      quotesDiv.innerHTML = "closed"
    }
  }

  def getWebsocketUri(document: Document): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}/update"
  }
}