package org.fayalite.ui.app.comm

import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.state.StateSync.ParseRequest
import org.fayalite.ui.app.state.Input
import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import rx._
import rx.core.Obs

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js._
import scala.util.Try

object PersistentWebSocket {

  // val cookies = document.cookie
  // case class Register(cookies: String)
 val host = org.scalajs.dom.document.location.host
  var pws = new PersistentWebSocket()

  case class PRTemp(

                   )

  def send(s: String) = pws.send(s)
/*
  def sendPR(pr: ParseRequest) = {
    import upickle._
    send(write(pr))
  }
*/

  def getWSURI = {
    val uri = "ws://" +
      org.scalajs.dom.document.location.host
    if (uri.endsWith("/")) uri.dropRight(1) else uri
  }

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
                          wsUri: String = PersistentWebSocket.getWSURI
                           ) {
  val onOpen: Var[Event] = Var(null.asInstanceOf[Event])
  val onClose: Var[Event] = Var(null.asInstanceOf[Event])
  val onError: Var[Event] = Var(null.asInstanceOf[Event])
  val message: Var[MessageEvent] = Var(null.asInstanceOf[MessageEvent])
  val parsedMessage = Var(null.asInstanceOf[Dynamic])
  val messageStr: Var[String] = Var(null.asInstanceOf[String])
  var open = Var(false)
 // haoyi li workbench

  def mkSocket = socket() = new WebSocket(wsUri)

  val socket = Var(new WebSocket(wsUri))

  var toSend = Array[String]()

  def send(s: String) = {
    if (open()) {
      Try {
        socket().send(s)
      }
    } else toSend = toSend :+ s
  }

/*  val heartBeat = Input.heartBeat.foreach{
    hb =>
      Schema.TryPrintOpt{
      //  println("heartbeat sent" + open())
        if (open()) send("heartbeat") //Window.metaData)
      }
  }*/
/*
  val msgPrinter = Obs(messageStr, skipInitial = true) {
    println("ws msg " + messageStr().slice(0, 100))
  }*/

  val socketWatch = Obs(socket) {
    val ws = socket()
    ws.onopen = (e: Event) => {onOpen() = e; open() = true; println("open") ;
    //  send("debug")
      toSend.foreach{send}
      toSend = Array()
    }
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
}

