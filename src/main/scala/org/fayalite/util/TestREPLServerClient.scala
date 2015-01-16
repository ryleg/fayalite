package org.fayalite.util

import org.fayalite.repl.REPL.Evaluate


/**
 * Created by ryle on 11/9/2014.
 */

object TestREPLRemote{
  def main(args: Array[String]) {
    val clientPort = 1400 + scala.util.Random.nextInt(5000)

    val client = new HackAkkaClient(port=clientPort)
    val replId = 1

    client.start(replId)

  //  Thread.sleep(10000) // Of course there's no ack

    val result = client.evaluate(Evaluate("val x = 1", replId))

    println(result)
  }
}

object TestREPLServerClient {

  def main (args: Array[String]) {


  val serverPort = 1400 + scala.util.Random.nextInt(5000)
  val clientPort = 1400 + scala.util.Random.nextInt(5000)

  val server = new HackAkkaServer(serverPort)

  val client = new HackAkkaClient(port=clientPort, masterServerPort = serverPort)

  Thread.sleep(3000)

    val replId = 1

    client.start(replId)

    Thread.sleep(10000) // Of course there's no ack

    val result = client.evaluate(Evaluate("val x = 1", replId))

    println(result)

  }


}
