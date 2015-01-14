package org.fayalite.repl

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success, Try}
import REPL._


class REPLHandler(serverPort: Int) extends Actor {

  var sparkManager : SparkREPLManager = _

  def receive = {
    case Initialize(userId: Int, remoteActorPath: RemoteActorPath) =>
      sparkManager = new SparkREPLManager()

    case x => println(s"replHandler received $x on serverPort: $serverPort")

  }

}