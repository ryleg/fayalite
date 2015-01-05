package org.fayalite.repl

import akka.actor.{Props, Actor, ActorRef}
import org.fayalite.util.RemoteAkkaUtils._

/**
 * Created by ryle on 12/12/2014.
 */

class EchoClient extends Actor {
  import EchoClient._
  import HackREPLManager.Register
  import akka.pattern._
  import akka.actor._

  val userName = "client0"

  def receive = {
    case RegistrationRequest(serverToRegisterWith) => serverToRegisterWith ! Register(userName)
    case x => println(s"From client: $x")
  }
}

object EchoClient {

  case class RegistrationRequest(registrationServer: ActorRef)

  def main(args: Array[String]) {

    implicit val actorSystem = createActorSystem(clientActorSystemName, defaultHost, defaultPort + 1)
    implicit val rap = RemoteActorPath(host="fayalite.org")
    val server = getActor()

    val client = actorSystem.actorOf(Props(new EchoClient()))

    client ! RegistrationRequest(server)
  }
}
