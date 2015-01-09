package org.fayalite.util

import org.scalatest.FunSuite
import org.fayalite.util.RemoteAkkaUtils.defaultPort

class TestDuplex extends FunSuite {

  test ("Duplex init and responses") {

    val one = new HackAkkaDuplex()

    val two = new HackAkkaDuplex(port=defaultPort+10)

    Seq(one, two).foreach{_.startClient()}

    one.remoteServer ! "yo"

    two.remoteServer ! "sup"


  }

}
