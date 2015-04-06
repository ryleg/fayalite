package org.fayalite.ui.app.canvas


import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Try}

import org.fayalite.ui.app.canvas.Schema._

/**
 * We're only using a single canvas / context to start with for simplicity, this will eventually become
 * the 'background' canvas when we move to multiple contexts. For now this is the sole
 * bottleneck to all canvas interactions.
 */
object Canvas {

  var canvas : dom.raw.HTMLCanvasElement = _
  var ctx : dom.CanvasRenderingContext2D = _
  var width = 0
  var height = 0

  var activeElem : Option[Elem] = None
  var elementTriggers : Map[Elem, () => Unit] = Map()
/*

  window.oninput = (e: Event) => {
    println("oninput" + e)
  }
  window.addEventListener("paste",
    (e: Event) => {
      println(e.valueOf())
      println(e.hasOwnProperty("clipboardData"))
    //  e.cast[ClipboardEvent]
  //    val dt = e.cast[dom.DataTransfer]
  //    println("dt types " + dt.types)
  //    println(dt.types.length)
  //    println(Array.tabulate(dt.types.length){i => dt.types.apply(i)})
//      println("dt " + dt.getData("pasteundefined"))
      println("paste" + e.cast[dom.DataTransfer].types )
      println("len " +  e.cast[dom.DataTransfer].getData("text/plain"))

  })
  //var elementListeners : Map
*/

  def drawText(s: String, x: Int, y: Int) = {
    ctx.font = "15pt Calibri"
    ctx.fillStyle = "white"
    ctx.fillText(s, x, y)
  }


  def draw(s: String) = {
    ctx.font = "14pt Calibri"
    ctx.fillStyle = "white"
    ctx.fillText(s, cursor, 100)
  }
  
  var cursor = 50
  val cursorDx = 50
  var curText = ""
  
  def testKeyBind() = {
    val attempt = Try {
      window.onkeypress = (ke: KeyboardEvent) => {
        val k = ke.keyCode.toChar.toString
        println("kp " + k)
        draw(k)
        cursor += cursorDx
        curText += k
      }
    }
    attempt match { case Failure(e) => e.printStackTrace(); case _ =>}
  }

  val xButtonBuffer = 10
  val yButtonBuffer = 10

  import rx._
  val onclick = Var(null.asInstanceOf[MouseEvent])
  val onresize = Var(null.asInstanceOf[UIEvent])

  val rightClick = Var(null.asInstanceOf[MouseEvent])
/*
  Obs(onclick) {
    println("obsonclickpure")
  }*/

  def resetCanvasTriggers() = {

    window.oncontextmenu = (me: MouseEvent) => {
      me.preventDefault()
      rightClick() = me
    }

    window.onclick = (me: MouseEvent) =>
    {
      onclick() = me
    //  println("onclick")
      val sxi = me.screenX
      val syi = me.screenY
      val cxi = me.clientX
      val cyi = me.clientY
  /*    println(s"Window onclick " + //screenX $sxi "screenY $syi  " +
        s"clientX $cxi clientY $cyi " +
        s"numTriggers: ${elementTriggers.size}")*/
      elementTriggers.foreach{
        case (elem, trigger) =>
          val isInside =
            (cxi > elem.position.x - xButtonBuffer) &&
            (cxi < elem.position.x2 + xButtonBuffer) &&
            (cyi > elem.position.y - yButtonBuffer) &&
            (cyi < elem.position.y2 + yButtonBuffer)
/*          println(s"isInside: $isInside $elem x2,y2" +
            s" ${elem.position.x2},${elem.position.y2}")*/
          if (isInside) {
            activeElem = Some(elem)
            trigger()
          }
      }
    }
  }

  def w = document.documentElement.clientWidth - 50 // wtf? it makes a scroll bar without this offset
  def h = document.documentElement.clientHeight - 50


  // TODO : Change to reactive.
  @deprecated
  def initCanvas() = {
    val elem = document.body.getElementsByTagName("canvas")
    canvas = {if (elem.length != 0) elem(0) else {
      val obj = dom.document.createElement("canvas")
      document.body.getElementsByTagName("div")(0).appendChild(obj)
      obj
    }}.asInstanceOf[dom.raw.HTMLCanvasElement]
    ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    canvas.width = w
    canvas.height = h
    width = canvas.width
    height = canvas.height
    window.onresize = (uie: UIEvent) => {
      canvas.width = w
      canvas.height = h
      width = canvas.width
      height = canvas.height
      DrawManager.onresize(uie)
      elementTriggers.foreach{_._1.draw()}
      //      println(s"resize canvas width: $width height: $height")
      onresize() = uie

    }

    testKeyBind()
  }

}
