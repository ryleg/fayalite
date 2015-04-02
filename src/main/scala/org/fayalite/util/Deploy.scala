package org.fayalite.util

import akka.actor.Actor
import org.apache.spark.Logging


/**
 * Actor deployment shortcuts
 */
object Deploy {

  /**
   * One line receive loop to actor.
   * @param rxFunc : def receive on actor
   * @param port : actorSystem port
   * @return : Wrapper around server / actorSystem for auxiliary use
   */
  def apply(rxFunc: PartialFunction[Any, Unit], port: Int) = {
    class Deployable() extends Actor with Logging {
      def receive = rxFunc
    }
    new SimpleRemoteServer({new Deployable()}, port)
  }

}
