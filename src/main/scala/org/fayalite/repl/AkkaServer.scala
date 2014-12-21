package org.fayalite.repl

/**
 * Created by ryle on 12/10/2014.
 */

import akka.actor.Props
import org.fayalite.util.SparkAkkaUtilsExample

object AkkaServer {

  def main(args: Array[String]) {

      implicit val actorSystem = SparkAkkaUtilsExample.serverInitialize()



  }

}
