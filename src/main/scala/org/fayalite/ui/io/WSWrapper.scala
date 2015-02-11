package org.fayalite.ui.io

import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import org.fayalite.ui.io.WSWrapper
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp

class WSWrapper(wsUri: String) {
  import rx._

  val open = Var(false)

  val ws = Var(new WebSocket(wsUri))
  initWS()

  def defaultParseMessageEvent = Unit

  val onMessage = Var(
    (me: MessageEvent) => {
  //    me.data.
      defaultParseMessageEvent
  })

  val reconnectionAttempts = Var(0)

  def attemptReconnect() = {
  //  if (reconnectionAttempts() < MAX_RECONNECT_ATTEMPTS)
    reconnectionAttempts() += 1
    ws() = new WebSocket(wsUri)
    initWS()
    Thread.sleep(1000)
  }

  def initWS() = {
    ws().onopen = (e: Event) => { open() = true; reconnectionAttempts() = 0 }
    ws().onclose = (e: Event) => {
      open() = false
      attemptReconnect()
    }
    ws().onerror = (e: Event) => open() = false; attemptReconnect()
    ws().onmessage = onMessage()
  }


}
