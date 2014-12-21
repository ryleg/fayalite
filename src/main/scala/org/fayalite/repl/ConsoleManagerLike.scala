package org.fayalite.repl

import java.io._

import akka.actor.{ActorRef, Actor}
import org.fayalite.repl.JSON.{REPLHistory, REPLRead, REPLWrite, REPLWriteHistory}
import org.fayalite.repl.REPL._

import scala.concurrent.Future

class ConsoleManager(client: ActorRef) extends ConsoleManagerLike {

  clientRelay = client

/*  def receive = {
    case text: String =>
      val byteData = text.map {_.toChar}.toCharArray.map {_.toByte}
      out.write(byteData)
      out.flush()
  }*/

}

trait ConsoleManagerLike {

  var clientRelay: ActorRef = _

  val in = scala.Console.in
  val out = scala.Console.out

  def readLoop() : Unit = {
    while (true) {
      val line = in.readLine ()
      println("In readloop of console manager, readLine: " + line)
      clientRelay.write(line + "\n")
    }
  }

}
