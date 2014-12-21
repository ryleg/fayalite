package org.fayalite.repl

import akka.actor.ActorSystem
import org.scalatest._
import REPL._


/**
 * Created by ryle on 11/9/2014.
 */

class TestREPLServerClient extends FunSuite {

  implicit val actorSystem = ActorSystem("TestREPLServerClient")

  val server = Server.start()
  val (listener, client) = Client.start()

  test("Starting server") {

    Thread.sleep(3000)

    client.write("val x = 1 \n")

    Thread.sleep(3000)

    client.read()
   // println(response)

  }
}
