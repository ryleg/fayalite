package org.fayalite.ui

import org.fayalite.ui.ws.Server
import Server.{SprayFrame, PipedMessage, SenderMap, RequestClients}
import org.fayalite.util.RemoteClient
import org.fayalite.repl.REPL._
import spray.can.websocket.frame.TextFrame

/**
 * Used for sending messages through a websocket pipe intermediary
 * bridge that feed to client browser js engine.
 */
class WebsocketPipeClient {

  val rc = new RemoteClient(scala.util.Random.nextInt(30000) + 10000)
  val sr = rc.getServerRef(16348)

  def sendFrame(sprayFrame: SprayFrame) = {
    sr.map{
      s =>
        val sm = s.??[Set[String]](RequestClients())
        sm.toList.map{
          sp =>
            s ! PipedMessage(sp, sprayFrame)
        }
    }

  }
}

/**
 * I can't remember if this is being used or not, probably don't use it?
 */
object WebsocketPipeClient {

  import Server.pipePort

  def sendBinary(binary: Int) = {}

  def sendFrame(sprayFrame: SprayFrame) = {

    val rc = new RemoteClient(scala.util.Random.nextInt(30000) + 10000)
    val sr = rc.getServerRef(16348)
    sr.map{
      s =>
        val sm = s.??[Set[String]](RequestClients())
        sm.toList.map{
            sp =>
            s ! PipedMessage(sp, sprayFrame)
        }
    }

  }
}
