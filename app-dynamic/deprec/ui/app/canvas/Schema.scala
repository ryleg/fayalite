package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.comm.PersistentWebSocket

import scala.scalajs.js
import scala.scalajs.js.Array
import scala.util.{Failure, Success, Try}

/**
 * Contracts for understanding websocket messages.
 */
object Schema {

  def TryPrintOpt[T](f : => T) = {
    Try{f} match {
      case Success(x) => //println("success");
       Some(x);
      case Failure(e) => e.printStackTrace(); None
    }
  }


}
