package org.fayalite.util

import akka.actor.Actor
import org.apache.spark.Logging

object Deploy {

  def apply(rxFunc: PartialFunction[Any, Unit], port: Int) = {
    class Deployable() extends Actor with Logging {
      def receive = rxFunc
    }
    new SimpleRemoteServer({new Deployable()}, port)
  }

}
