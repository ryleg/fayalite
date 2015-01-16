package org.fayalite.repl

import org.fayalite.repl.REPL._
import org.fayalite.util.{HackAkkaClient, HackAkkaServer}
import org.scalatest._


/**
 * Created by ryle on 11/9/2014.
 */

class TestREPLClientAgainstRemoteJVM extends FunSuite {

  val client = new HackAkkaClient(port=31415)

  test ( "Evaluate x = 1 through actor pipe") {

    val replId = 1

  //  client.start(replId)

  //  Thread.sleep(10000) // Of course there's no ack

    val result = client.evaluate(Evaluate("val x = 1", replId))

    println(result)

  }


}
