package org.fayalite.ui

import org.fayalite.ui.SimpleServer.{WebsocketPipeMessage, SenderMap, RequestClients}
import org.fayalite.util.RemoteClient
import org.fayalite.repl.REPL._
import spray.can.websocket.frame.TextFrame

object WebsocketPipeClient {

  import SimpleServer.pipePort

  def main(args: Array[String]) {


    val rc = new RemoteClient(15909)
    val sr = rc.getServerRef(16348)
    sr.foreach{
      s =>
        val sm = s.??[Set[String]](RequestClients())
        println(sm)
        s ! "asdf"
        s ! WebsocketPipeMessage("boguspath", TextFrame("newmesg"))
        sm.toList.foreach{
            sp =>
            println("sending msg that wont get thru to sender " + sp)
        //    sa ! "msg wont get thru"

            s ! WebsocketPipeMessage(sp, TextFrame("newmesg"))
        }
    }
    Thread.sleep(Long.MaxValue)

  }
}
