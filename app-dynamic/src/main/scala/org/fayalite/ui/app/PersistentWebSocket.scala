package org.fayalite.ui.app

import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import scala.concurrent.Future
import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js._
import scala.scalajs.js.Dynamic.{global => g}
import scala.util.{Failure, Try}

object PersistentWebSocket {

  // val cookies = document.cookie
  // case class Register(cookies: String)

  var pws = new PersistentWebSocket()

  // def send(msg: js.Any) = pws.ws.send(JSON.stringify(msg))

  def sendKV(k: String, v: String) : Unit = {
    def send() = pws.ws.send(
      s"""{"$k": "$v", "cookies":"${document.cookie}"}"""
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
  }



}

class PersistentWebSocket(
                          wsUri: String = DisposableWebSocket.WS_URI
                           ) {

  var onmessage = (me: MessageEvent) => {
    println("Persistent default msg " + me.data.toString)
  }
  var open = false


  val defaultOnOpen = (e: Event) => {
    PersistentWebSocket.sendKV("tab", "register")
    open = true
    //ws.send(JSON.stringify(Map("cookies" -> document.cookie)))
  }

  import PersistentWebSocket._
  val ws = new WebSocket(wsUri)
    ws.onopen = defaultOnOpen
    ws.onclose = (e: Event) => {
      println("PersistentWebSocket closed")
    }
    ws.onerror = (e: Event) =>
      println("PersistentWebSocket error")
    ws.onmessage = (me: MessageEvent) => {
        val attempt = Try{
            onmessage(me)
        }
        attempt match {
          case Failure(e) => println("PersistentWebSocket failure") ; e.printStackTrace()
          case _ => println("PersistentWebSocket success")
        }
    }
  }

