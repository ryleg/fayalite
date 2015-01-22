package org.fayalite.util

import java.util.Calendar

import RemoteAkkaUtils._
import akka.actor.{Actor, Props, ActorRef}
import org.apache.spark.Logging
import org.fayalite.repl.REPL._
import org.fayalite.repl.Supervisor

import scala.concurrent.{Await, Future}
import scala.util.Try


class HackAkkaDuplex(
                        val host: String = defaultHost,
                        val port: Int = defaultPort
                        )  {

    val serverActorSystem = createActorSystem(serverActorSystemName, host, port)

    //Only used by notebook client, repl server stores references to RemoteActorRef]
    var remoteServer : ActorRef = _

    def startClient(temporaryClientActorSystemPort: Int,
                    remoteServerPort: Int) = {
      implicit val clientActorSystem = createActorSystem(
        clientActorSystemName,
        host,
        temporaryClientActorSystemPort)
      implicit val rap = RemoteActorPath(port = remoteServerPort)
      remoteServer = getActor()
      remoteServer
    }

}

abstract class DuplexPipe extends Logging {
  val port: Int
  val duplex : HackAkkaDuplex
}

object HackAkkaServer {

  def main(args: Array[String]) {

    new HackAkkaServer ()

    Thread.sleep(Long.MaxValue)

  }
}

class HackAkkaServer(val port: Int = defaultPort) extends DuplexPipe {
  //Initialize static reference.
  SparkReference.getSC
  val duplex = new HackAkkaDuplex(port=port)
  duplex.serverActorSystem.actorOf(Props(new Supervisor(duplex)), name=serverActorName)
  logInfo("Started server on port " + port)

}

class NotebookClient(duplex: HackAkkaDuplex) extends Actor {

  //God no

  var messages : List[(Output, java.util.Date)] = List()

  def receive = {
    case output : Output =>
      val now = Calendar.getInstance().getTime
      messages = (output, now) :: messages
    case h: Heartbeat =>
      duplex.remoteServer ! h
    case _ => sender ! messages
  }
}
import akka.pattern.ask

class HackAkkaClient(
                      val notebookId : Int,
                      val port: Int = defaultPort + 10,
                     val masterServerPort: Int = defaultPort) extends DuplexPipe
with Logging {

  type Messages = List[(Output, java.util.Date)]

  val duplex = new HackAkkaDuplex(port=port)

  val client = duplex.serverActorSystem.actorOf(Props(new NotebookClient(duplex)), name=serverActorName)

  val remoteServer = duplex.startClient(port + 2000, masterServerPort)

  def start(replId: Int) : Unit = {
    remoteServer ! Start(port, replId)
  }

  def poll() = client.??[List[(Output, java.util.Date)]]("")

  def pollTestLog() = Future {
    while (true) {
      Thread.sleep(5000)
      logInfo("Polling: " + poll())
    }
  }

  import scala.concurrent.duration._
  def pollWait(fEval : => String, timeout : Duration = 10.seconds) = {
    val prevResult = poll()
    var currentResult = poll()

    fEval
    // scala.rx is way better pattern than this.
    val attempt = Future {
      do {
        Thread.sleep(1000)
        currentResult = poll()
      } while (prevResult == currentResult)
    }

    val res = Await.result(attempt, timeout).toString
    res
  }

  def evaluate(code: String, userId: Int, replId: Int) = {
    val evaluationParams = SuperInstruction(code, replId, userId, notebookId, port)
  /*  val prevHistory = poll()
    var checkHistory  = poll()*/
    duplex.remoteServer ! evaluationParams
/*    val check = Future {
      do {
        Thread.sleep(200)
        checkHistory  = poll()
      } while(checkHistory == prevHistory)
      checkHistory
    }
    check.getAs[Messages]*/
  }
}
