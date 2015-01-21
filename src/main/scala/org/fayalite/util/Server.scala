
package org.fayalite.util

import java.io.OutputStream
import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props, Actor}
import akka.io.Tcp.{Write, Register, Received}
import akka.io.{Tcp, IO}
import akka.util.{ByteString, ByteIterator, Timeout}
import org.json4s._
import org.fayalite.repl.REPL._
import akka.actor._
import scala.concurrent.duration._
import org.fayalite.util.RemoteAkkaUtils


object Server{

  val portOffset = 16180

  def main(args: Array[String]) {
    implicit val as =  RemoteAkkaUtils.createActorSystem(port=portOffset)
    start()
  }

  def start()(implicit as: ActorSystem) : ActorRef = {

    as.actorOf(Props(new Server()))
  }

}



class Server extends Actor {

  import Server._
  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", portOffset+1))

//  val replM = context.actorOf(Props(new HackREPLManager()))

  def receive = {
    case b @ Bound(localAddress) =>
    // do some logging or setup ...
      println(s"bound @ $b")

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      println(s"Connected remote $remote local $local")
   //   val handler = context.actorOf(Props(new REPLHandler(replM)))
      val connection = sender
    //  connection ! Register(handler)
  }

}
