package org.fayalite.ui.app.canvas


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Try}

import org.fayalite.ui.app.canvas.Schema._

object Canvas {

  var canvas : dom.HTMLCanvasElement = _
  var ctx : dom.CanvasRenderingContext2D = _
  var width = 0
  var height = 0


  var elementTriggers : Map[Elem, () => Unit] = Map()


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

  def resetCanvasTriggers() = {
    window.onclick = (me: MouseEvent) =>
    {
      val sxi = me.screenX
      val syi = me.screenY
      val cxi = me.clientX
      val cyi = me.clientY
      println(s"Window onclick " + //screenX $sxi "screenY $syi  " +
        s"clientX $cxi clientY $cyi " +
        s"numTriggers: ${elementTriggers.size}")
      elementTriggers.foreach{
        case (elem, trigger) =>
          val isInside =
            (cxi > elem.position.x - xButtonBuffer) &&
            (cxi < elem.position.x2 + xButtonBuffer) &&
            (cyi > elem.position.y - yButtonBuffer) &&
            (cyi < elem.position.y2 + yButtonBuffer)
          println(s"isInside: $isInside $elem x2,y2" +
            s" ${elem.position.x2},${elem.position.y2}")
          if (isInside) {
            trigger()
          }
      }
    }
  }

  def initCanvas() = {
    canvas = document.body.getElementsByTagName("canvas").headOption.getOrElse {
      val obj = dom.document.createElement("canvas")
      document.body.appendChild(obj)
      obj
    }.cast[dom.HTMLCanvasElement]
    ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    width = canvas.width
    height = canvas.height
    window.onresize = (uie: UIEvent) => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      width = canvas.width
      height = canvas.height
      DrawManager.onresize(uie)
      //      println(s"resize canvas width: $width height: $height")
    }

    testKeyBind()
  }

}
