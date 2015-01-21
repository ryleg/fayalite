package org.fayalite.util

import org.fayalite.repl.REPL.{SuperInstruction, Evaluate}

import scala.concurrent.Future


/**
 * Created by ryle on 11/9/2014.
 */

object TestREPLRemote{
  def main(args: Array[String]) {

    val clientPort = 1400 + scala.util.Random.nextInt(5000)


    val client = new HackAkkaClient(1, port=clientPort) //, masterServerPort = 15029)

    val result = client.evaluate(s"val x = 5", 0, 1)



    import org.fayalite.repl.REPL._

    client.pollTestLog()

    var x = 0
    while (true) {
      Thread.sleep(3000)
      client.evaluate(s"val x = $x", 0, 1)

    }

    Thread.sleep(Long.MaxValue)
    /*
        1 to 10 foreach { i =>
          val result = client.evaluate(s"val x = $i", 1, 1)
          Thread.sleep(1000)
          1 to 5 foreach { j =>
            Thread.sleep(1000)
            val resp = client.poll()
            println(j + "  " + resp.slice(0, 1))
          }
        }*/

  }
}

object TestREPLServer {

  def main (args: Array[String]) {


    val serverPort = 15029//1400 + scala.util.Random.nextInt(5000)
    val server = new HackAkkaServer(serverPort)

    Thread.sleep(Long.MaxValue)

    }


}

object TestREPLServerClient {

  def main (args: Array[String]) {


  val serverPort = 1400 + scala.util.Random.nextInt(5000)
  val clientPort = 1400 + scala.util.Random.nextInt(5000)

  val server = new HackAkkaServer(serverPort)

  val client = new HackAkkaClient(1, port=clientPort, masterServerPort = serverPort)

    1 to 10 foreach { i =>
      val result = client.evaluate(s"val x = $i", 1, 1)

      println(result)

    }
  }


}
