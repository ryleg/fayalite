package org.fayalite.util

import java.util.Calendar

import RemoteAkkaUtils._
import akka.actor.{Actor, Props, ActorRef}
import org.apache.spark.Logging
import org.fayalite.repl.REPL._
import org.fayalite.repl.Supervisor


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

abstract class DuplexPipe {
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

}

class NotebookClient extends Actor {

  //God no
  var lastMessage : (String, java.util.Date) = ("init",  Calendar.getInstance().getTime)

  def receive = {
    case Output(evaluationResult) =>
      val now = Calendar.getInstance().getTime
      lastMessage = (evaluationResult, now)
    case _ => {
      sender ! lastMessage
    }
  }
}
import akka.pattern.ask

class HackAkkaClient(
                      val port: Int = defaultPort + 10,
                     val masterServerPort: Int = defaultPort) extends DuplexPipe
with Logging {


  val duplex = new HackAkkaDuplex(port=port)

  val client = duplex.serverActorSystem.actorOf(Props(new NotebookClient()), name=serverActorName)

  val remoteServer = duplex.startClient(port + 2000, masterServerPort)

  def start(replId: Int) : Unit = {
    remoteServer ! Start(port, replId)
  }

  def evaluate(evaluationParams: Evaluate) : String = {
    val (lastMessage, prevTime) = client.??[(String, java.util.Date)]("")
    duplex.remoteServer ! evaluationParams
    var sleeps = 0
    while (true) {
      Thread.sleep(200)
      sleeps = sleeps + 1
      if (sleeps % 50 == 0) logInfo("client awaiting response") // 10 seconds
      if (sleeps > 100) return "failed"
      val (checkMessage, checkTime) = client.??[(String, java.util.Date)]("")
      if (checkTime.after(prevTime)) return checkMessage
    }
    "failure?"
  }

}
