package org.fayalite.ui.app.comm

import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import rx._
import rx.core.Obs

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js._
import scala.util.Try

object PersistentWebSocket {

  // val cookies = document.cookie
  // case class Register(cookies: String)

  var pws = new PersistentWebSocket()

}
  // TODO : Switch to upickle once errors are resolved.
//  def sendV(v: String) = sendKV("tab", v)

/*  def sendKV(k: String, v: String, f: Dynamic => Unit = (d: Dynamic) => (),
             seqKV: Seq[(String, String)] = Seq()): String = {
    val id = Random.nextInt().toString
    val kvStr = {Seq(("requestId", id)) ++ seqKV}.map{
      case (ks,vs) => s""""$ks": "$vs""""}.mkString(",")

    def send() = pws.ws.send(
      s"""{"$k": "$v", $kvStr, "cookies":"${document.cookie}"}"""
    )
    if (pws.open) {
      send()
    }
    else {
      pws.ws.onopen = (e: Event) => {
        pws.defaultOnOpen(e)
        send()
      }
    }

    // return future of function here on timeout also.
    // set an obs on the future to terminate the obs on parsed message
    val o: Obs = Obs(parsedMessage) {
      Try {
          if (parsedMessage().requestId.toString == id) {
            f(parsedMessage())
        }
      }
    }
    id*/


class PersistentWebSocket(
                          wsUri: String = "ws://localhost:8080" //DisposableWebSocket.WS_URI
                           ) {
  val onOpen: Var[Event] = Var(null.asInstanceOf[Event])
  val onClose: Var[Event] = Var(null.asInstanceOf[Event])
  val message: Var[MessageEvent] = Var(null.asInstanceOf[MessageEvent])
  val parsedMessage = Var(null.asInstanceOf[Dynamic])
  val messageStr: Var[String] = Var(null.asInstanceOf[String])
  var open = Var(false)

  val socket = Var(new WebSocket(wsUri))

  val socketWatch = Obs(socket) {
    val ws = socket()
    ws.onopen = (e: Event) => onOpen() = e
    ws.onclose = (e: Event) => onClose() = e
    ws.onerror = (e: Event) =>
      println("PersistentWebSocket error")
    ws.onmessage = (me: MessageEvent) => {
      Try {
        message() = me
        messageStr() = me.data.toString
        parsedMessage() = JSON.parse(me.data.toString)
      }
    }
  }
}

