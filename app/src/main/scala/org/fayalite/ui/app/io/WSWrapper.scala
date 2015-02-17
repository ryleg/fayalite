package org.fayalite.ui.app.io

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

  var onMessage =  (me: MessageEvent) => {
  //    me.data.
    //  defaultParseMessageEvent
    println(me.data)
  }

  var reconnectionAttempts = 0

  def attemptReconnect() = {
  //  if (reconnectionAttempts() < MAX_RECONNECT_ATTEMPTS)
    reconnectionAttempts += 1
    ws = new WebSocket(wsUri)
    initWS()
    Thread.sleep(1000)
  }

  def initWS(): Unit = {
    ws.onopen = (e: Event) => {
      open = true; reconnectionAttempts = 0
      ws.send("init")
    }
    ws.onclose = (e: Event) => {
      open = false
      attemptReconnect()
    }
    ws.onerror = (e: Event) => open = false; attemptReconnect()
    ws.onmessage = onMessage
  }


}
