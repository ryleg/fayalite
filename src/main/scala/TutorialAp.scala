/**
 * Created by ryle on 1/29/15.
 */
package tutorial.webapp
import org.scalajs.dom
import org.scalajs.dom.window
import org.scalajs.dom.{MouseEvent, Event, document, WebSocket}
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.jquery.jQuery
import org.scalajs.dom.extensions._
import scala.scalajs.js
import js.Dynamic.{ global => g }

object TutorialApp extends JSApp {

  def main(): Unit = {
    val pxy =  (me: MouseEvent) =>
 {
   println( me.clientX -> me.clientY)
 }

    window.onmousemove = pxy
    window.oninput
    jQuery("body").append("<p>[message]</p>")
    // Create the canvas
    val canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    val ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    ctx.fillStyle="red"
    ctx.fillRect(10,10,50,50)
    val imgData=ctx.getImageData(10,10,50,50)
    ctx.putImageData(imgData,10,70)

    document.body.appendChild(canvas)

    //val canv = document.createEl("canvas")
    //document.body.appendChild(canv)
//    document.body.onmousemove = pxy

    val ws = new WebSocket("ws://localhost:8080/")
//    ws.send("wtf")
    ws.onmessage = (x: Any) => println("onmessage: " + x)
    ws.onopen = (x : Any) => {
      println("onopen: " + x)
      ws.send("opened on client")
    }

    val pl = (x: String) => (y: Any) => println(s"$x: $y")
    ws.onclose = pl("onclose")
    ws.onerror = pl("onerror")

  }
}
