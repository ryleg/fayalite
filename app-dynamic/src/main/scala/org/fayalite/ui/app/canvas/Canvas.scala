package org.fayalite.ui.app.canvas


import PositionHelpers.LatCoordD
import org.fayalite.ui.app.manager.Editor
import org.fayalite.ui.app.state.Input
import org.scalajs.dom
import org.scalajs.dom._
import rx._

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
  val rect = Var(getRect)

  // change to lift
  val onKeyDown = Var(null.asInstanceOf[KeyboardEvent])
  val onKeyUp = Var(null.asInstanceOf[KeyboardEvent])
  val onclick = Var(null.asInstanceOf[MouseEvent])
  val onresize = Var(null.asInstanceOf[UIEvent])
  val rightClick = Var(null.asInstanceOf[MouseEvent])


  window.onkeydown = (ke: KeyboardEvent) => {onKeyDown() = ke}
  window.onkeyup = (ke: KeyboardEvent) => onKeyUp() = ke
  window.oncontextmenu = (me: MouseEvent) => {
    me.preventDefault()
    rightClick() = me
  }
  window.onclick = (me: MouseEvent) => {
    onclick() = me
  }

  // NO
  val ctrlKey = Var(false)
  Obs(onKeyDown, skipInitial = true) {
    if (onKeyDown().ctrlKey) ctrlKey() = true
  }
  Obs(onKeyUp, skipInitial = true) {
    if (onKeyUp().ctrlKey) ctrlKey() = false
  }
  Obs(onresize, skipInitial = true) {
    widthR() = canvas.width
    heightR() = canvas.height
    rect() = getRect
  }

  def getRect = document.body.getBoundingClientRect()
  def w = document.documentElement.clientWidth - 18 // wtf? it makes a scroll bar without this offset
  def h = document.documentElement.clientHeight - 50

  val canvasR = Var(null.asInstanceOf[dom.raw.HTMLCanvasElement])
  val ctxR = Var(null.asInstanceOf[dom.CanvasRenderingContext2D])

  val heightR = Var(0D)
  val widthR = Var(0D)
  val area = Rx { LatCoordD(widthR(), heightR())}

  Obs(canvasR, skipInitial = true) {
    if (canvasR() != null) {
      heightR() = canvasR().height.toDouble
      widthR() = canvasR().width.toDouble
    }
  }

  def createCanvas() = {
    val styling = "background-color:#2B2B2B; "
    document.body.setAttribute("style", styling)
    val elem = document.body.getElementsByTagName("canvas")
    canvas = {if (elem.length != 0) elem(0) else {
      val obj = dom.document.createElement("canvas")
      val sa = obj.setAttribute("style", styling)
      document.body.appendChild(obj)
      obj
    }}.asInstanceOf[dom.raw.HTMLCanvasElement]
    ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  }

  // TODO : Change to reactive.
  @deprecated
  def initCanvas() = {
    createCanvas()
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
      //      println(s"resize canvas width: $width height: $height")
      onresize() = uie
      rect() = getRect
    }
    println(Input.t)
    println(Editor.editor)
  }

}
