package org.fayalite.repl

/**
 * Created by ryle on 11/10/2014.
 */

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.io.Tcp.Connected
import akka.io.{IO, Tcp}
import akka.util.{Timeout, ByteString}
import org.apache.spark.{SparkConf, SparkContext, SparkEnv}
import akka.actor._
import scala.concurrent.Await
import akka.pattern.ask
import scala.concurrent.duration._

import JSON._
import REPL._

import scala.util.Try


object Client {

 // var consoleManager : ActorRef = _

  /*
 import org.fayalite.repl.Client
 Client.main(Array(""))
   */

  def start()(implicit as: ActorSystem) : (ActorRef, ActorRef) = {
    val host = new InetSocketAddress("localhost", 16180)
    val listener = as.actorOf(Props(new Listener()))
    val client = as.actorOf(Client.props(host, listener))
    (listener, client)
  }

  /**
   *
   * @param args
   */
  def main(args: Array[String]) {

    implicit val as = ActorSystem("a")
    val (listener, client) = start()
    val consoleManager = new ConsoleManager(client)
    consoleManager.readLoop()

  }

  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}


class Listener extends Actor {

 // var consoleManager : ActorRef = _

  def receive = {
    case c @ Connected(remote, local) =>
        println("listener connected")

    case x : ByteString => println("inside listener receive " + x); //Try{Client.consoleManager ! x}
      val response = x.decodeString("UTF-8")
      println("inside listener: recieved string: " + response)
   // case ar : ActorRef => Client.consoleManager = ar; println("registered console manager")

  }

}


class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  this.sender

  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) =>
      println("client connected " + c)
      listener ! c
      val connection = sender
      connection ! Register(self)
      context become {
        case data: ByteString =>
  //        val replHandlerResponse : String =
            connection ! Write(data)
        //  println(s"replHandlerResponse $replHandlerResponse")

        case CommandFailed(w: Write) =>
          // O/S buffer was full
          listener ! "write failed"
        case Received(data) =>
          println("recieved data at client actor")
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context stop self
      }
  }
}