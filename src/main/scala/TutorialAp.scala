/**
 * Created by ryle on 1/29/15.
 */
package tutorial.webapp

import org.fayalite.repl.JSON
import org.scalajs.dom
import org.scalajs.dom._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.jquery.jQuery
import org.scalajs.dom.extensions._
import scala.scalajs.js
import js.Dynamic.{ global => g }

object TutorialApp extends JSApp {

  def main(): Unit = {
    var open = false
    val ws = new WebSocket("ws://localhost:8080/")
    object s {
      def s(msg: String) = if(open) ws.send(msg) else println("WS CLOSED! failed " +
        s"to send msg $msg")
    }
    object i {
      def m (k: String) = (me: MouseEvent) => s s s"$k,${me.clientX},${me.clientY}"
      def k(kt: String) = (ke: KeyboardEvent) => s s s"$kt,${ke.key}"
    }

    val pl = (x: String) => (y: Any) => println(s"$x: $y")
    ws.onmessage = (me: MessageEvent) =>
    {
      println("onmessage: " + me.toLocaleString())
    }
    ws.onopen = (e: Event) => open = true
    ws.onclose = (e: Event) => open = false
    ws.onerror = (e: Event) => open = false

    window.onmousemove = i m "move"
    window.onmousedown = i m "down"
    window.onmouseup = i m "up"
    window.onkeydown = i k "down"
    window.onkeyup = i k "up"


    val canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    val ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    document.body.appendChild(canvas)

  //  val imgData=ctx.getImageData(10,10,50,50)
//    ctx.putImageData(imgData,10,70)
  }
}
