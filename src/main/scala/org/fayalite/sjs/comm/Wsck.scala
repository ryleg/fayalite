/*
package org.fayalite.ui.app.comm

import org.scalajs.dom._
import rx._

import scala.scalajs.js.JSON
import scala.util.Try

class Wsck {

  private def setup(ws: WebSocket, v: Array[Var[T]]) = {
    ws.onopen = (e: Event) => {}
    ws.onclose = (e: Event) => {onClose() = e; open() = false; println("closed")}
    ws.onerror = (e: Event) => {onError() = e; open() = false ; println("wserr" + e.toString)}
    ws.onmessage = (me: MessageEvent) => {
      Try {
        message() = me
        messageStr() = me.data.toString
        parsedMessage() = JSON.parse(me.data.toString)
      }
    }
  }

  val socket = Var({
    val ws = new WebSocket("ws://localhost:8080"))
  }

}
*/
