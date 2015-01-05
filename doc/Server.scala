
package org.fayalite.repl

import java.io.OutputStream
import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props, Actor}
import akka.io.Tcp.{Write, Register, Received}
import akka.io.{Tcp, IO}
import akka.util.{ByteString, ByteIterator, Timeout}
import org.json4s._
import REPL._
import akka.actor._
import scala.concurrent.duration._
import org.fayalite.util.RemoteAkkaUtils


object Server{

  val portOffset = 16180

  def main(args: Array[String]) {
    implicit val as =  RemoteAkkaUtils.createActorSystem()
    start()
  }

  def start()(implicit as: ActorSystem) : ActorRef = {

    as.actorOf(Props(new Server(0, 0D)))
  }

}


class Server(user: Int, weight: Double, useSparkILoop: Boolean = false) extends Actor {
  implicit val timeout = Timeout(15 seconds)

  import Server._
  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", portOffset+user))

  val replM = context.actorOf(Props(new HackREPLManager()))

  def receive = {
    case b @ Bound(localAddress) =>
    // do some logging or setup ...
      println(s"bound @ $b")

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      println(s"Connected remote $remote local $local")
      val handler = context.actorOf(Props(new REPLHandler(replM)))
      val connection = sender
      connection ! Register(handler)
  }

}
