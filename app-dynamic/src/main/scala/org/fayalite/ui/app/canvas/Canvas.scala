package org.fayalite.ui.app.canvas


import org.fayalite.ui.app.canvas.elem.PositionHelpers.LatCoordD
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

  import rx._
  // change to lift
  val onKeyDown = Var(null.asInstanceOf[KeyboardEvent])
  window.onkeydown = (ke: KeyboardEvent) => {onKeyDown() = ke}

  val onKeyUp = Var(null.asInstanceOf[KeyboardEvent])
  window.onkeyup = (ke: KeyboardEvent) => onKeyUp() = ke

  val pasteEvent = Var(null.asInstanceOf[Event])
  window.addEventListener("paste",
    (e: Event) => {
     // def clipboardData(event: dom.Event): dom.DataTransfer = js.native
      pasteEvent() = e
/*
      //  e.cast[ClipboardEvent]
          val dt = e.asInstanceOf[dom.DataTransfer]
          println("dt types " + dt.types)
          println(dt.types.length)
      //    println(Array.tabulate(dt.types.length){i => dt.types.apply(i)})
      //      println("dt " + dt.getData("pasteundefined"))
          println("len " +  dt.getData("text/plain"))*/
    }
  )



/*  Obs(onKeyDown) {
    println("onkeypress")
   // TryPrintOpt{println(onKeyDown().keyCode)}
  }*/
  val ctrlKey = Var(false)

  Obs(onKeyDown, skipInitial = true) {
    if (onKeyDown().ctrlKey) ctrlKey() = true
  }
  Obs(onKeyUp, skipInitial = true) {
    if (onKeyUp().ctrlKey) ctrlKey() = false
  }


  val xButtonBuffer = 10
  val yButtonBuffer = 10

  import rx._
  val onclick = Var(null.asInstanceOf[MouseEvent])
  val onresize = Var(null.asInstanceOf[UIEvent])

  val rect = Var(getRect)

  Obs(onresize, skipInitial = true) {
    widthR() = canvas.width
    heightR() = canvas.height
    rect() = getRect
  }

  def getRect = document.body.getBoundingClientRect()



  val rightClick = Var(null.asInstanceOf[MouseEvent])

  window.oncontextmenu = (me: MouseEvent) => {
    me.preventDefault()
    rightClick() = me
  }

  window.onclick = (me: MouseEvent) => {
    onclick() = me
  }

  def w = document.documentElement.clientWidth - 18 // wtf? it makes a scroll bar without this offset
  def h = document.documentElement.clientHeight - 50

  val canvasR = Var(null.asInstanceOf[dom.raw.HTMLCanvasElement])
  val ctxR = Var(null.asInstanceOf[dom.CanvasRenderingContext2D])
  val heightR = Var(0D)
  Obs(canvasR, skipInitial = true) {
    if (canvasR() != null) {
      heightR() = canvasR().height.toDouble
      widthR() = canvasR().width.toDouble
    }
  }

  val widthR = Var(0D)

  val area = Rx { LatCoordD(widthR(), heightR())}

  /*
          * { padding: 0; margin: 0; }
        html, body, canvas {
        min-height: 100% !important;
        min-width: 100%;
        height: 100%;
        width: 100%;
        background-color:#2B2B2B;
        }
   */
  // TODO : Change to reactive.
  @deprecated
  def initCanvas() = {
    val styling = "" + // position: absolute; " +
/*      "" + //"left: 0; top: 0;
      "padding: 0; margin: 0; " +
    "min-height: 100%; " +
    "min-width: 100%; " +
    "height: 100%; " +
    "width: 100%; " +*/
    "background-color:#2B2B2B; "
    document.body.setAttribute("style", styling)
    val elem = document.body.getElementsByTagName("canvas")
    canvas = {if (elem.length != 0) elem(0) else {
      val obj = dom.document.createElement("canvas")
      val sa = obj.setAttribute("style", styling)
      document.body.appendChild(obj)
      obj
    }}.asInstanceOf[dom.raw.HTMLCanvasElement]
    ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]


    canvas.width = w
    canvas.height = h
    canvasR() = canvas
    ctxR() = ctx

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
  }

}
