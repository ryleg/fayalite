package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Schema.ParseResponse
import org.fayalite.ui.app.canvas.{Schema, Canvas}
import org.scalajs.dom.{Event, MessageEvent, WebSocket}
import rx.core.Obs
import scala.concurrent.Future
import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js._
import scala.scalajs.js.Dynamic.{global => g}
import scala.util.{Random, Failure, Try}
import rx._

object PersistentWebSocket {

  // val cookies = document.cookie
  // case class Register(cookies: String)

  var pws = new PersistentWebSocket()
  import StateSync._

  val metaData = Var(null.asInstanceOf[Dynamic])

  Obs(metaData) {
      val md = metaData()
/*      List("access", "secret", "pem").map{k =>
      Try{md().user.selectDynamic(k).toString()}.toOption.map{
        u =>
          Canvas.elementTriggers.filter{_._1.name.startsWith(k)}.map{_._1.redrawText(u)}
      }
    }*/
    //Try{println("metaData Obs " + metaData().user.email.toString)}
  }

  // def send(msg: js.Any) = pws.ws.send(JSON.stringify(msg))

  def sendV(v: String) = sendKV("tab", v)

  def sendKV(k: String, v: String, f: Dynamic => Unit = (d: Dynamic) => (),
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
    id
  }


  val message: Var[MessageEvent] = Var(null.asInstanceOf[MessageEvent])
  lazy val parsedMessage = Var(null.asInstanceOf[Dynamic])
  val messageStr : Var[String] = Var(null.asInstanceOf[String])

  lazy val parseLi = Rx {
      val msg = messageStr()
 //   upickle.json.read(msg).
      //Try{upickle.read[ParseResponse](msg)}.toOption
    }

}

class PersistentWebSocket(
                          wsUri: String = DisposableWebSocket.WS_URI
                           ) {
  import PersistentWebSocket._

  var onmessage = (me: MessageEvent) => {
    message() = me
    messageStr() = me.data.toString
    val jv = JSON.parse(me.data.toString)
//    println("Persistent default msg " + me.data.toString)
    val pm = JSON.parse(me.data.toString)

    parsedMessage() = pm

    pm.flag.toString match {
      case "eval" => Dynamo.eval(pm)
      case "heartbeat" => //println{"dynamic heartbeat"}
      case "auth" => pm.email
        val email = pm.email.toString
        HeaderNavBar.setEmail(email)
        metaData() = pm
      // Try{ println("metaData pseudoObs " + metaData().toString)}

     //   println("auth email: " + email)
      case _ =>
        println("can't recognize command2 code from: " + me.data.toString.slice(0,200))
    }

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
          case _ => // println("PersistentWebSocket success")
        }
    }
  }

