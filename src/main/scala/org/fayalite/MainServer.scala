package org.fayalite

import fa._
import org.fayalite.ui.ParseServer
import org.fayalite.ui.ws.Server

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

/**
 * Entry point to single machine synthetic cluster run. Inside same JVM for now.
 */
object MainServer {

  /**
   * Brings up websocket bridge and user state parser.
   * @param args: Unused
   */
  def main(args: Array[String]) {

    Future{ParseServer.main(args)}
    Server.main(args)
  }

  def runViaSBTSeparateJVM = {
    val sbr: Seq[String] = Seq("sbt", "run")
    Future{(sbr ++ Seq(ParseServer.getClass.getCanonicalName)).!!}
    (sbr ++ Seq(Server.getClass.getCanonicalName)).!!
  }

}
