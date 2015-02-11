
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp

object EntryApp extends JSApp {

  val WS_URI = "ws://localhost:8080/"
  var open = false

  def main(): Unit = {

    implicit val ws = new WebSocket(WS_URI)

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

    var sid = ctx.getImageData(0, 0, w, h)
    println("w " + w + " h " + h)

    var canvaspixelarray = sid.data;


    var canvaspixellen = canvaspixelarray.length;
    var bytearray = new Uint8Array(canvaspixellen);

    for (i <- 0 until canvaspixellen) {
      bytearray(i) = canvaspixelarray(i)
    }

    println("sidlen " + sid.data.length)

    // canvas.style.opacity = "0.2"
//    println(sid.data.mkString(","))

    ws.onmessage = (me: MessageEvent) => {
    //  println("onmessage " + me.toLocaleString())
  /*          val sd=  me.data.toString
       //     println("onmessage data  " + sd)
            val sm = sd.split(",")
            val nimd = sm.tail.map{_.toInt}
            println(nimd.length + " nimd")
            var imgData=sid
            // invert colors
            for (i4 <- 0 until imgData.data.length) yield
            {
              val i = i4*4
              val rgba =
              imgData.data(i) = 255-nimd(i);
              imgData.data(i+1)=255-nimd(i+1);
              imgData.data(i+2)=255-nimd(i+2);
              imgData.data(i+3)=255;
            }
            ctx.putImageData(imgData,0,0)
      
            println("onmsglen " + sm.length)
            sm.tail.map {
              _.toInt
            }.zipWithIndex.foreach{
              case (vi, idx) =>
                sid.data(idx) = vi
            }
            //var imageData = ctx.createImageData(dat)
         //   imageData
            ctx.putImageData(sid,0,0)*/
    }
    ws.onopen = (e: Event) => {
  /*    open = true
      var imgData=sid.data
      var bytearray = new Uint8Array(imgData.length);

      imgData.zipWithIndex.foreach{
        case (dat, idx) => bytearray(idx) = dat
      }

      ws.send(bytearray.buffer);
   //   ws.send(imgData.data.mkString(","))*/
      }

    ws.onclose = (e: Event) => open = false
    ws.onerror = (e: Event) => open = false


/*   */

  }
}

