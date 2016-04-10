package org.fayalite.ui.ws

import akka.actor.{ActorRef, Actor}
import org.fayalite.ui.ws.Server.{RequestClients, PipedMessage, SenderMap}

/**
 * Client to Websocket to Akka / Other JVMs tunnel
 * @param allSenders
 */
class Pipe(allSenders: SenderMap) extends Actor {
  def receive = {
    case PipedMessage(senderPath, message) =>
      println("attempting to send client msg " + senderPath + " msg: " + message)
      allSenders.foreach{
        case (sp, s) =>
          println("found sender")
          s ! message //.asInstanceOf[spray.can.websocket.frame.Frame]
      }
    /* allSenders.get(senderPath).foreach{
         s =>
           println("found sender")
           s ! message //.asInstanceOf[spray.can.websocket.frame.Frame]
       }*/
    case RequestClients() =>
      println("requestClients")
      sender ! allSenders.keys.toSet
    case _ =>
      println("websocketpipe")
  }
}
