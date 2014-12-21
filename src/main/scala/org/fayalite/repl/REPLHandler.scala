package org.fayalite.repl

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.fayalite.repl.JSON.REPLWrite
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success, Try}
import REPL._



class REPLHandler(replM: ActorRef) extends Actor {
  implicit val timeout = Timeout(15 seconds)

  def receive = {

    case Received(data) =>
      val attempt = Try {

        val message = data.decodeString("UTF-8")
        println("decoded version " + message)
        val parsedMessage = JSON.messageParse(message)

        val thisResponse = parsedMessage match {
          case m: REPLWrite => replM ! m; "writ"
          case x => {
            val response: String = replM ? x
            response
          }
        }
        println(s"response to client: $thisResponse")
        sender ! Write(ByteString(thisResponse))

      }

      attempt match {
        case Success(x) => println("Server loop didn't fail try \n")
        case Failure(x) => x.printStackTrace()
      }

  }

}