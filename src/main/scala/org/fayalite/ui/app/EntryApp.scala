
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.fayalite.ui.io.WSWrapper
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp

object EntryApp extends JSApp {

  val WS_URI = "ws://localhost:8080/"

  def main(): Unit = {

    implicit val wsw = new WSWrapper(WS_URI)

    Canvas.initCanvas()
    /*
    object s {
      def s(msg: String) = if(open) ws.send(msg) else {
      //  println("WS CLOSED! failed to send msg $msg")
      }
    }
    object i {
      def m (k: String) = (me: MouseEvent) => s s s"$k,${me.clientX},${me.clientY}"
      def k(kt: String) = (ke: KeyboardEvent) => s s s"$kt,${ke.key}"
    }

    val pl = (x: String) => (y: Any) => println(s"$x: $y")*/

  }
}

