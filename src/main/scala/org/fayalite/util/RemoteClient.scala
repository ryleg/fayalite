package org.fayalite.util

import org.fayalite.util.RemoteAkkaUtils._

import scala.util.{Failure, Try}
import fa._


/**
 * Quick and dirty Spark-Akka compatible actor system / actor wrapper
 * for case class cross-jvm communication. Needs substantial fixes.
 * @param port
 */
class RemoteClient(port: Int=rport) {
  val actorSystem = createActorSystem(clientActorSystemName, defaultHost, port)

  /**
   * Find another JVM on the same machine on a supplied port.
   * This is just for cross-jvm test talk. Doesn't actually work with a custom
   * host because as you'll notice, that's hardcoded.
   * @param remotePort
   * @return
   */
  def getServerRef(remotePort: Int) = {
    val at = Try {
      val actorPath = s"akka.tcp://$serverActorSystemName" +
        s"@$defaultHost:$remotePort/user/" +
        s"$serverActorName"
     // import akkaTimeout
      val actor = actorSystem.actorSelection(actorPath).resolveOne()
      val actorRef = actor.get
      actorRef
    }
    at match { case Failure(e) => e.printStackTrace(); case _ => }
    at.toOption
  }
}