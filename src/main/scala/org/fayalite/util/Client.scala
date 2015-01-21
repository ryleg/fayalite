package org.fayalite.util

/**
 * Created by ryle on 11/10/2014.
 */

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.io.Tcp.Connected
import akka.io.{IO, Tcp}
import akka.util.{Timeout, ByteString}
import org.apache.spark.{Logging, SparkConf, SparkContext, SparkEnv}
import akka.actor._
import org.fayalite.repl.{JSON, REPL}
import scala.concurrent.Await
import akka.pattern.ask
import scala.concurrent.duration._

import JSON._
import REPL._

import scala.util.Try


class ClientManager(implicit as: ActorSystem)  {

  val host = new InetSocketAddress("localhost", 16180)
  val listener = as.actorOf(Props(new Listener()))
  val client = as.actorOf(Client.props(host, listener))
  (listener, client)

  def evaluate(code: String)(implicit ev: NotebookParams = defaultNotebookParams)  = {
   // val superInstruction = SuperInstruction(code, ev.replId, ev.userId, ev.notebookId)
  //  val request =  ClientRequest(superInstruction)
   // client.??[String](request)
  }

}

object Client {

  def main(args: Array[String]) {
    implicit val as = ActorSystem("a")
    val cm = new ClientManager()
 //   (1 to 10) foreach { cm.evaluate("val x = 1") }
  }

  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}


class Listener extends Actor with Logging {

  var serverProxy : ActorRef = null

  def receive = {
    case c @ Connected(remote, local) =>
        logInfo("Listener connected")
        serverProxy = sender
    case x : ByteString => logInfo("inside listener receive " + x); //Try{Client.consoleManager ! x}
      val instruction = x.decodeString("UTF-8")
      val parsedInstruction = JSON.parseSuperInstruction(instruction)
      logInfo("inside listener: recieved string: " + instruction + parsedInstruction)
    case ClientRequest(instr) =>

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