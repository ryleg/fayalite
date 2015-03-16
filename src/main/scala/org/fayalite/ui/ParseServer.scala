package org.fayalite.ui

import akka.actor.{Actor, Props}
import org.fayalite.aws.ServerManager
import org.fayalite.util.{JSON, SimpleRemoteServer, RemoteAkkaUtils}
import spray.can.websocket.frame.{BinaryFrame, TextFrame}

import scala.util.{Failure, Success, Try}

object ParseServer {
  def main(args: Array[String]) {
    val sr = new SimpleRemoteServer({new ParseServer()} ,20000)
    Thread.sleep(Long.MaxValue)
  }
  case class ParseRequest(
                         tab: String,
                         cookies: String
                           )
}

class ParseServer extends Actor{
  import ParseServer._

  def receive = {
    case msg: String =>
      println("message : " + msg)
      implicit val formats = JSON.formats
      val pmsg = JSON.parse4s(msg).extract[ParseRequest]
      val response = pmsg.tab match {
        case "Servers" =>
          Some(ServerManager.requestServerInfo())
        case _ => println("no tab match")
          None
      }
      response.foreach{r => sender() ! TextFrame(r)}
    case TextFrame(msg) =>
      val umsg = msg.utf8String
      println("parse message" + umsg)
    //  sender() ! TextFrame("parsed")
    case BinaryFrame(dat) =>
      //TODO : Check how to binary serialize dom event input classes here.
      println("binaryframe.")
  }
}
