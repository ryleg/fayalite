package org.fayalite.ui.ws

import akka.actor.ActorRef
import org.fayalite.ui.ws.Server._
import spray.can.websocket.frame.{TextFrame, BinaryFrame}

/**
 * Isolated tunnel ops interception.
 */
object Bottleneck {

  def rx(value: String, ref: ActorRef) = {
    attemptParse(value, ref)
  }

}
