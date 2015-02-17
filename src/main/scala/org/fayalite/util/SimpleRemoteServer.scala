package org.fayalite.util

import akka.actor.{Actor, Props}


class SimpleRemoteServer(actorDeploy: => Actor, port: Int) {

  import RemoteAkkaUtils._

  val actorSystem = createActorSystem(serverActorSystemName, defaultHost, port)
  val deployedActor = actorSystem.actorOf(Props(actorDeploy), name=serverActorName)

}
