package org.fayalite.ui

import akka.actor.ActorRef
import rx.core._
import spray.can.websocket.frame.TextFrame

import scala.collection.mutable

object LoopRxWS {

  val lastMsg = Var("")

  val clients = Var(new mutable.MutableList[ActorRef])

  //Obs(ClientMessageToResponse.parsedMessage) {

  //}

  def registerSender(sender: ActorRef) = {
    if (!clients().contains(sender)) clients() += sender
   // sender ! "registered"
  }

  def onMessage(msg: String, sender: ActorRef): Unit = {
    println("onMessage from browser to pipe server " + msg)
    registerSender(sender)
    lastMsg() = msg
  }

}
