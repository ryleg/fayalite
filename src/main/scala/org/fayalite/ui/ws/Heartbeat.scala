package org.fayalite.ui.ws

import akka.actor.ActorRef
import spray.can.websocket.frame.TextFrame

import scala.concurrent.Future
import scala.util.Try

object Heartbeat {

  var started: Boolean = false

  implicit val ec =  org.fayalite.Fayalite.ec

  def startHeartbeats() = (allSenders: scala.collection.mutable.Map[String, ActorRef]) => {
    if (!started) {
      println("starting heartbeats")
      Future {
        Try {
          while (true) {
            Thread.sleep(10000)
            allSenders.foreach {
              case (sp, s) => s ! TextFrame( """{"flag": "heartbeat"}""")
              //      println("sent heartbeat")
            }
          }
        }
      }
    }
    started = true
  }

}


/*
            parseServer match {
              case Some(parseServerActorRef) =>
                val response = Try{
                  parseServerActorRef.??[spray.can.websocket.frame.Frame](x)
                }.toOption
                response.foreach{
                  r => send(r)
                }
              case None =>
                println("parse server not found on request, re-attempting connection")
                //TODO: Make this so much less dangerous
                parseServer = parser.getServerRef
            }
  */
