package org.fayalite.util

import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.repl.REPL._
import org.fayalite.util.RemoteAkkaUtils._

import scala.util.{Failure, Try}

class RemoteClient(port: Int) {
  val actorSystem = createActorSystem(clientActorSystemName, defaultHost, port)

  def getServerRef(remotePort: Int) = {
    val at = Try {
      val actorPath = s"akka.tcp://$serverActorSystemName" +
        s"@$defaultHost:$remotePort/user/" +
        s"$serverActorName"

      val actor = actorSystem.actorSelection(actorPath).resolveOne()
      val actorRef = actor.get
      actorRef
    }
    at match { case Failure(e) => e.printStackTrace(); case _ => }
    at.toOption
  }

}