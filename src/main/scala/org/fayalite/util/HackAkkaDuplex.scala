package org.fayalite.util

import RemoteAkkaUtils._
import akka.actor.{Props, ActorRef}
import org.fayalite.repl.REPLHandler

class HackAkkaDuplex(
                        val host: String = defaultHost,
                        val port: Int = defaultPort
                        )  {

    val serverActorSystem = createActorSystem(serverActorSystemName, host, port)

    val replHandler = serverActorSystem.actorOf(Props(new REPLHandler(port)), name=serverActorName)

    var remoteServer : ActorRef = _

    def startClient() = {
      implicit val clientActorSystem = createActorSystem(clientActorSystemName, host, port + 1)
      implicit val rap = RemoteActorPath(port = port)
      remoteServer = getActor()
    }

}

abstract class DuplexPipe {
  val duplex : HackAkkaDuplex
}

class HackAkkaServer extends DuplexPipe {

  val duplex = new HackAkkaDuplex()

}

class HackAkkaClient extends DuplexPipe {

  val duplex = new HackAkkaDuplex(port = defaultPort + 10)

}
