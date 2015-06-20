package org.fayalite

import fa._
import org.fayalite.ui.{ParseServer, ws}

import scala.concurrent.Future

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
    ws.Server.main(args)
  }

}
