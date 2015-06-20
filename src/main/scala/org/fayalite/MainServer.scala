package org.fayalite

import org.fayalite.ui.{ParseServer, ws}
import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._
import ExecutionContext.Implicits.global

/**
 * Entry point to single machine synthetic cluster run. Inside same JVM for now.
 */
object MainServer {

  /**
   * Brings up websocket bridge and user state parser.
   * @param args: Unused
   */
  def main(args: Array[String]) {
    val sbr: Seq[String] = Seq("sbt", "run")
    Future{(sbr ++ Seq(ParseServer.getClass.getCanonicalName)).!!}
    (sbr ++ Seq(ws.Server.getClass.getCanonicalName)).!!
  }

}
