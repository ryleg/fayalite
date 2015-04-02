package org.fayalite.ui.app

import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.{JSON, JSApp}

import scala.util.{Failure, Try}

class WebSocketWrapper(wsUri: String) {

  var open = false
  var reconnectionAttempts = 0
  implicit var ws = new WebSocket(wsUri)
  initWS()

  def initWS(): Unit = {
    ws.onopen = (e: Event) => {
      open = true; reconnectionAttempts = 0
      ws.send("reload")
      //ws.send(new Uint8Array(1).buffer)
      ws.binaryType = "arraybuffer"
    }
    ws.onclose = (e: Event) => {
      println("dynamicwebsocket closed")
      open = false
    }
    ws.onerror = (e: Event) =>
      println("dynamicwebsocket error")
      open = false; //attemptReconnect()
    ws.onmessage = (me: MessageEvent) => {
        val attempt = Try{
      //    println("me.data " + me.data.toString)
          val pm = JSON.parse(me.data.toString)
          pm.flag.toString match {
            case "eval" =>
         //     println("main dynamic call")
              js.eval(pm.code.toString) //+ "\n  org.fayalite.ui.app.DynamicEntryApp().main();")
             // val retVal =
     //         js.eval("org.fayalite.ui.app.DynamicEntryApp().main();")
       //       println("main dynamic call2")
              val curBridge = "dynamic-bridge"
              //js.eval("org.fayalite.ui.app.DynamicEntryApp().fromBridge('yo');")
              val retVal = js.eval(s"org.fayalite.ui.app.DynamicEntryApp().fromBridge('$curBridge');")
                  //                s"fromBridge(${org.fayalite.ui.app.Bridge.x});")
              println("retVal " + retVal)
            case "heartbeat" => println{"dynamic heartbeat"}
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
