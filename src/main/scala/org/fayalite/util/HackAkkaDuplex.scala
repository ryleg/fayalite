package org.fayalite.util

import java.util.Calendar

import RemoteAkkaUtils._
import akka.actor.{Actor, Props, ActorRef}
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

class HackAkkaServer extends DuplexPipe {

  val port = defaultPort

  //Initialize static reference.
  SparkReference.getSC

  val duplex = new HackAkkaDuplex(port=port)

  duplex.serverActorSystem.actorOf(Props(new Supervisor(duplex)), name=serverActorName)

}

class NotebookClient extends Actor {

  case class CheckResult()
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
class HackAkkaClient extends DuplexPipe {

  val port = defaultPort + 10

  val duplex = new HackAkkaDuplex(port=port)

  val client = duplex.serverActorSystem.actorOf(Props(new NotebookClient()), name=serverActorName)

  def evaluate(evaluationParams: Evaluate) : String = {
    val (lastMessage, prevTime) = client ?? _
    duplex.remoteServer ! evaluationParams


  }

}
