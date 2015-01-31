package org.fayalite.ui

import akka.actor.ActorRef

object LoopRxWS {
  def onMessage(msg: String, sender: ActorRef): Unit = {
    sender ! msg


  }

}
