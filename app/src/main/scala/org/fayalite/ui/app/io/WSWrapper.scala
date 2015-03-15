package org.fayalite.ui.app.io

import org.fayalite.ui.app.Canvas
import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.{JSON, JSApp}
import org.fayalite.ui.app.io.InputCapture.registerInputListeners

import scala.util.{Failure, Try}

class WSWrapper(wsUri: String) {

  var open = false

  implicit var ws = new WebSocket(wsUri)
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
      ws.send("init")
      registerInputListeners(ws)
      //ws.send(new Uint8Array(1).buffer)
      ws.binaryType = "arraybuffer"
    }
    ws.onclose = (e: Event) => {
      open = false
    }
    ws.onerror = (e: Event) => open = false; //attemptReconnect()
    ws.onmessage = (me: MessageEvent) => {
      val isBinaryFrame = me.data.isInstanceOf[dom.ArrayBuffer]
      if (isBinaryFrame) {
        val validMessage = me.data.cast[dom.ArrayBuffer]
        val byteArray1 = new Uint8Array(validMessage)
        Canvas.setCanvasDataFromBytes(byteArray1)
      }
      else{
        val attempt = Try{
       //   println("me.data " + me.data.toString)
          val pm = JSON.parse(me.data.toString)
          pm.flag.toString match {
            case "eval" =>
              println("main dynamic call")
              js.eval(pm.code.toString + "\n  org.fayalite.ui.app.DynamicEntryApp().main();")
             // val retVal =
              js.eval("org.fayalite.ui.app.DynamicEntryApp().main();")
              println("main dynamic call2")
              val curBridge = org.fayalite.ui.app.Bridge.x
              println("fromBridge call using curBridge: " + curBridge)
              js.eval("org.fayalite.ui.app.DynamicEntryApp().fromBridge('yo');")
              val retVal = js.eval(s"org.fayalite.ui.app.DynamicEntryApp().fromBridge('$curBridge');")


                  //                s"fromBridge(${org.fayalite.ui.app.Bridge.x});")
              println("retVal " + retVal)
            case _ =>
              println("can't recognize command code from: " + me.data.toString)
          }
        }

        attempt match {
          case Failure(e) => println("eval failure") ; e.printStackTrace()
          case _ => println("eval success")
        }

      }
    }
  }


}
