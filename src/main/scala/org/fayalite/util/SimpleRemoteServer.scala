package org.fayalite.util

import akka.actor.{Actor, Props}

/**
 * The dumbest possible spark compatible actor deployment possible.
 * For an even dumber way to get an actorDeploy, see Deploy object.
 * @param actorDeploy : Something that gives us an actor by construction.
 * @param port: A port, like for TCP you know.
 */
class SimpleRemoteServer(actorDeploy: => Actor, port: Int) {

  import RemoteAkkaUtils._
  val actorSystem = createActorSystem(serverActorSystemName, defaultHost, port)
  val deployedActor = actorSystem.actorOf(Props(actorDeploy), name=serverActorName)

}
