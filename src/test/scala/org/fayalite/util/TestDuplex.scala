package org.fayalite.util

import akka.actor.{Props, Actor}
import org.scalatest.FunSuite
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.repl.REPL._


class TestDuplex extends FunSuite {

  test ("Duplex init and responses") {

    class PrintActor extends Actor {
      def receive = {
        case Yo(msg) => println(s"received: $msg")
      }
    }

    val one = new HackAkkaDuplex()

    one.serverActorSystem.actorOf(
      Props(new PrintActor()), name=serverActorName)

    val two = new HackAkkaDuplex(port=defaultPort+10)

    two.serverActorSystem.actorOf(
      Props(new PrintActor()), name=serverActorName)

    Seq(one, two).zipWithIndex.foreach{
      case (d, idx) => d.startClient(
        defaultPort+1000+idx, defaultPort)}

    case class Yo(sup: String)

    one.remoteServer ! Yo("hay")

    two.remoteServer ! Yo("case classes seem to work here")


  }

}
