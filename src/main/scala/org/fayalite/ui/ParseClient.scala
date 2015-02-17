package org.fayalite.ui

import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.repl.REPL._

import scala.util.Try


class ParseClient(port: Int) {
  val actorSystem = createActorSystem(clientActorSystemName, defaultHost, port)

  def getServerRef = {
    Try {
      val actorPath = s"akka.tcp://$serverActorSystemName" +
        s"@$defaultHost:$defaultPort/user/" +
        s"$serverActorName"

      val actor = actorSystem.actorSelection(actorPath).resolveOne()
      val actorRef = actor.get
      actorRef
    }.toOption
  }


}

object ParseClient {
    def parseClient() ={
        new ParseClient(defaultPort+51)
    }
}
