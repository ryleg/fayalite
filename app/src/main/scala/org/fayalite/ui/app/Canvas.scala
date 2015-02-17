package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp


object Canvas {

  var canvas : dom.HTMLCanvasElement = _
  var ctx : dom.CanvasRenderingContext2D = _
  var defaultImageData : ImageData = _ // ctx.getImageData(0,0,width,height)
  var defaultImageDataLength : Int = _

  var width = 0
  var height = 0

  def initCanvas() = {
    canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    document.body.appendChild(canvas)
    val ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    val w = canvas.width
    val h = canvas.height
    width = w
    height = h
    ctx.strokeStyle = "red"
    ctx.lineWidth = 2;
    // Fill the path
    ctx.fillStyle = "#9ea7b8"
    ctx.fillRect(0, 0, w, h)
    defaultImageData = ctx.getImageData(0,0,width,height)
    defaultImageDataLength = defaultImageData.data.length
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

  def setCanvasData(me: MessageEvent) = {
    val validMessage = me.data.isInstanceOf[dom.ArrayBuffer]
    if (validMessage) {
      var bytearray = new Uint8Array(me.data.cast[dom.ArrayBuffer])
      for (i <- 8 until defaultImageDataLength) {
        val bval = bytearray(i)
        defaultImageData.data(i) = bval.toInt
      }
      ctx.putImageData(defaultImageData, 0, 0)
    }
    validMessage
  }
}
