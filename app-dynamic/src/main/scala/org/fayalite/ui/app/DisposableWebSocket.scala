package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Canvas
import org.scalajs.dom.{Event, MessageEvent, WebSocket}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSON
import scala.util.{Failure, Try}

object DisposableWebSocket {
  val WS_URI = "ws://localhost:8080/"



  def reload(command: String = "reload"
                     ) = {

    PersistentWebSocket.pws.ws.close()
    Canvas.elementTriggers.foreach{
      _._1.position.clear()
    }

    val ds = new DisposableWebSocket(command,
      (me: MessageEvent) =>
      {
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
    //    println("retVal " + retVal)
      case "heartbeat" => //println{"dynamic heartbeat"}
      case _ =>
        println("can't recognize command code from: " + me.data.toString)
    }
  }
    )
  }

}

class DisposableWebSocket(
                          message: js.Any,
                           onmessage: (MessageEvent => Unit),
                          wsUri: String = DisposableWebSocket.WS_URI
                           ) {

  val ws = new WebSocket(wsUri)
    ws.onopen = (e: Event) => {
      ws.send(message)
    }
    ws.onclose = (e: Event) => {
      println("DisposableWebSocket closed")
    }
    ws.onerror = (e: Event) =>
      println("DisposableWebSocket error")
    ws.onmessage = (me: MessageEvent) => {
        val attempt = Try{
          onmessage(me)
        }
        attempt match {
          case Failure(e) => println("DisposableWebSocket failure") ; e.printStackTrace()
          case _ => println("DisposableWebSocket success")
        }
      ws.close()
    }
  }

