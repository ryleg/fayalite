package org.fayalite.ui

import org.fayalite.ui.ws.WSServer
import WSServer.{SprayFrame, WebsocketPipeMessage, SenderMap, RequestClients}
import org.fayalite.util.RemoteClient
import org.fayalite.repl.REPL._
import spray.can.websocket.frame.TextFrame

class WebsocketPipeClient {

  val rc = new RemoteClient(scala.util.Random.nextInt(30000) + 10000)
  val sr = rc.getServerRef(16348)

  def sendFrame(sprayFrame: SprayFrame) = {
    sr.map{
      s =>
        val sm = s.??[Set[String]](RequestClients())
        sm.toList.map{
          sp =>
            s ! WebsocketPipeMessage(sp, sprayFrame)
        }
    }

  }
}

object WebsocketPipeClient {

  import WSServer.pipePort

  def sendBinary(binary: Int) = {}

  def sendFrame(sprayFrame: SprayFrame) = {

    val rc = new RemoteClient(scala.util.Random.nextInt(30000) + 10000)
    val sr = rc.getServerRef(16348)
    sr.map{
      s =>
        val sm = s.??[Set[String]](RequestClients())
        sm.toList.map{
            sp =>
            s ! WebsocketPipeMessage(sp, sprayFrame)
        }
    }

  }
}
