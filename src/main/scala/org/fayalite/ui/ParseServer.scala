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
      println("binaryframe.")


      val attempt = Try {
        println("pipeparser " + x)
        val response = ClientMessageToResponse.parse(x.toString)
        println("response " + response)
        response
      }
      sender ! (attempt match {
        case Success(xs) => xs
        case Failure(e) => e.printStackTrace()
      })
  }
}


object ParseServer {

  import RemoteAkkaUtils._

  def parseServer() = {


    val actorSystem = createActorSystem(serverActorSystemName, defaultHost, defaultPort)
    val sch = actorSystem.actorOf(Props(new TServ()), name=serverActorName)

  }



}
