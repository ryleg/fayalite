package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp


object Canvas {

  var canvas : dom.HTMLCanvasElement = _
  var ctx : dom.CanvasRenderingContext2D = _

  def initCanvas() = {
    canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    document.body.appendChild(canvas)
    val ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    val w = canvas.width
    val h = canvas.height
    ctx.strokeStyle = "red"
    ctx.lineWidth = 2;
    // Fill the path
    ctx.fillStyle = "#9ea7b8"
    ctx.fillRect(0, 0, w, h)
  }

  def getCanvasData = {
    val sid = ctx.getImageData(0, 0, canvas.width, canvas.height)
    val canvaspixelarray = sid.data
    val canvaspixellen = canvaspixelarray.length
    val bytearray = new Uint8Array(canvaspixellen)
    for (i <- 0 until canvaspixellen) {
      bytearray(i) = canvaspixelarray(i)
    }
    bytearray
  }

  def setCanvasData()

}
