package org.fayalite.ui

import akka.actor.{ActorRef, Actor, Props}
import org.fayalite.repl.SparkSupervisor
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.util.{HackAkkaClient, HackAkkaDuplex, DuplexPipe, SparkReference}
import rx.core.Obs

import org.fayalite.repl.REPL._
import spray.can.websocket.frame.TextFrame

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Success, Failure, Try, Random}

object Duplex {

  import rx._

  val a = Var("")

  def main(args: Array[String]) {
    val c = new ClientDuplex()
 //   c.register()
  //  c.rref ! "asdf"
    c.register()
  //  c.rref ! "zxckalsdk"
    Thread.sleep(Long.MaxValue)
  }
}

class UIPipeClient(duplex: HackAkkaDuplex) extends Actor {
  import Duplex._

 // ClientMessageToResponse.startWatch()

  def receive = {
    case x =>
      val attempt = Try {
        println("pipeparser " + x)
        //a() = x.toString
        val response = ClientMessageToResponse.parse(x.toString)
        println("response " + response)
        duplex.remoteServer ! response
      }
      attempt match {
        case Success(x) => println("succesfful parse")
        case Failure(e) => e.printStackTrace()
      }
  }
}


class ClientDuplex(val port : Int = 14050) extends DuplexPipe {
  val duplex = new HackAkkaDuplex(port=port)
  duplex.serverActorSystem.actorOf(Props(new UIPipeClient(duplex)), name=serverActorName)
  val rref = duplex.startClient(Random.nextInt(15000) + 5000, 14032)
  def register () = rref ! port
  logInfo("Started UI client parser on port " + port)

}

class UIPipe(duplex: HackAkkaDuplex) extends Actor {

  println("starting ui pipe server")
  def receive = {
    case x =>
      println("ui pipe server msg " + x)
      x match {
        case clientPort: Int =>
          println("UI pipe Server " + clientPort)
          val tempASClientPort = 15000 + 100 * scala.util.Random.nextInt(15) + 42
          val client = duplex.startClient(tempASClientPort, clientPort.toString.toInt)
          Obs(LoopRxWS.lastMsg) {
            println("UI pipe server sending parse msg " +
              "to parse client thru obs msg =" + LoopRxWS.lastMsg())
            client ! LoopRxWS.lastMsg()
          }
         /* val o =         Future {
              Try {

            }
          }.onComplete(e => println({"observation finished" + (e.get match {
              case Failure(e) => e.printStackTrace()
              case Success(x) => x
            })}))*/
        case parsedResponse: String =>
          println("ui pipe server got parsed response " + parsedResponse)
          ClientMessageToResponse.parsedMessage() = parsedResponse
          LoopRxWS.clients().foreach{
            c =>
              println(" SEDNING  to j s cleintparesdresponse")
              c ! TextFrame(parsedResponse)
          }

        case _ =>
      }
  }
}


  class Duplex(val port : Int = 14032) extends DuplexPipe {
    val duplex = new HackAkkaDuplex(port=port)
    duplex.serverActorSystem.actorOf(Props(new UIPipe(duplex)), name=serverActorName)
    logInfo("Started UI server on port " + port)

  }

