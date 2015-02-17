package org.fayalite.ui

import akka.actor.{Actor, Props}
import org.fayalite.util.RemoteAkkaUtils
import spray.can.websocket.frame.{BinaryFrame, TextFrame}

import scala.util.{Failure, Success, Try}

class ParseServer extends Actor{

  def receive = {
    case TextFrame(msg) =>
      val umsg = msg.utf8String
    case BinaryFrame(dat) =>
      //TODO : Check how to binary serialize dom event input classes here.
      println("binaryframe.")
  }
}


object ParseServer {

  import RemoteAkkaUtils._

  def parseServer() = {
    val actorSystem = createActorSystem(serverActorSystemName, defaultHost, defaultPort)
    val sch = actorSystem.actorOf(Props(new ParseServer()), name=serverActorName)
    sch
  }

}
