package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp


object Canvas {
  def initCanvas() = {
    val canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
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
}
