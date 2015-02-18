package org.fayalite.ui.app.io

import org.fayalite.ui.app.Canvas
import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp

class WSWrapper(wsUri: String) {

  var open = false

  var ws = new WebSocket(wsUri)
  initWS()

  def defaultParseMessageEvent = Unit

  var reconnectionAttempts = 0

  def attemptReconnect() = {
  //  if (reconnectionAttempts() < MAX_RECONNECT_ATTEMPTS)
    reconnectionAttempts += 1
    ws = new WebSocket(wsUri)
    ws.binaryType = "arraybuffer"
    initWS()
 //   Thread.sleep(1000)
  }

  def initWS(): Unit = {
    ws.onopen = (e: Event) => {
      open = true; reconnectionAttempts = 0
      ws.send(new Uint8Array(1).buffer)
    }
    ws.onclose = (e: Event) => {
      open = false
  //    attemptReconnect()
    }
    ws.onerror = (e: Event) => open = false; //attemptReconnect()
    ws.onmessage = (me: MessageEvent) => {
      println(ws.binaryType)
      ws.binaryType = "arraybuffer"
      println(ws.binaryType)

      Canvas.setCanvasData(me)
  //    if (!validCanvasData) {
    //    println("me.data " + me.data.toString)
  //    }
      //    me.data.
      //  defaultParseMessageEvent
    //  println("onmsg")
    //  println("me.data " + me.data.toString)
    }
  }


}
