package org.fayalite.ui.app.comm

import org.scalajs.dom._
import rx._
import rx.core.Obs

import scala.scalajs.js.{JSON, Dynamic}
import scala.util.Try

object Disposable {

    def send(s: String): Var[String] = {
      val socket = new WebSocket(
        "ws://" + window.location.host)
      val message: Var[MessageEvent] = Var(null.asInstanceOf[MessageEvent])
      val parsedMessage = Var(null.asInstanceOf[Dynamic])
      val messageStr: Var[String] = Var(null.asInstanceOf[String])
      socket.onopen = (e: Event) => socket.send(s)
      socket.onmessage = (me: MessageEvent) => {
        Try {
          message() = me
          messageStr() = me.data.toString
          }
    //      parsedMessage() = JSON.parse(me.data.toString)
          socket.close()
      }
      messageStr
    }
}
