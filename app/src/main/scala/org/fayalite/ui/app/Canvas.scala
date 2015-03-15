package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp
import scala.util.{Failure, Try}

// send JS over websocket, then eval inside browser.
object Canvas {

  var canvas : dom.HTMLCanvasElement = _
  var ctx : dom.CanvasRenderingContext2D = _
  var defaultImageData : ImageData = _ // ctx.getImageData(0,0,width,height)
  var defaultImageDataLength : Int = _

  var width = 0
  var height = 0

  def resetCanvasAfterResize = {
    cursor = 50
    curText.map{
      chr =>
        draw(chr.toString)
        cursor += dx
    }

  }

  def initCanvas() = {
    canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    document.body.appendChild(canvas)
    ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    width = canvas.width
    height = canvas.height

    window.onresize = (uie : UIEvent) => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      width = canvas.width
      height = canvas.height
//      println(s"resize canvas width: $width height: $height")
      resetCanvasAfterResize
    }

 //   ctx.strokeStyle = "red"
 //   ctx.lineWidth = 2;
    // Fill the path
 //   ctx.fillStyle = "black" //#9ea7b8"
 //   ctx.fillRect(0, 0, w, h)
    defaultImageData = ctx.getImageData(0,0,width,height)
    defaultImageDataLength = defaultImageData.data.length

   // testBijectiveImageDataTransform()
    testKeyBind()
  }

  var cursor = 50
/*
  ctx.font = "bold 10pt Calibri";
  ctx.fillText("Hello World!", 150, 100);
  ctx.font = "italic 40pt Times Roman";
  ctx.fillStyle = "blue";
  ctx.fillText("Hello World!", 200, 150);
  ctx.font = "60pt Calibri";
  ctx.lineWidth = 4;
  ctx.strokeStyle = "blue";
  ctx.strokeText("Hello World!", 70, 70);
 */

  def draw(s: String) = {
    ctx.font = "14pt Calibri"
    ctx.fillStyle = "white"
    ctx.fillText(s, cursor, 100)
  }

  val dx = 50
  var curText = ""
  def testKeyBind() = {
    val attempt = Try {
      window.onkeypress = (ke: KeyboardEvent) => {
        val k = ke.keyCode.toChar.toString
        println("kp " + k)
        draw(k)
        cursor += dx
        curText += k
      }
    }
    attempt match { case Failure(e) => e.printStackTrace(); case _ =>}
  }

  def testBijectiveImageDataTransform() = {
    println("testBijectiveImageDataTransform")

    val byteArray = getCanvasData
    ctx.moveTo(0,0)
    ctx.lineTo(200,100)
    ctx.stroke()
    def run = {
      println("ran")
      setCanvasDataFromBytes(byteArray)
    }

    org.scalajs.dom.setTimeout(() => run, 3000)

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

  def setCanvasDataFromBytes(byteArray: Uint8Array) = {
    println("set canvas data")
    for (i <- 8 until defaultImageDataLength) {
      val bval = byteArray(i)
      defaultImageData.data(i) = bval.toInt
    }
    ctx.putImageData(defaultImageData, 0, 0)
  }


  def setCanvasData(me: MessageEvent) = {
      val validMessage = me.data.cast[dom.ArrayBuffer]
      println(s"validMessage " + validMessage)
    //  println(s"validMessagebyteLength " + validMessage.byteLength)
      println(s"validMessageslice " + validMessage.slice(0, 10))

     val byteArray1 = new Uint8Array(validMessage) //me.data.cast[dom.ArrayBuffer])
    println(s"byteArray1 " + byteArray1)
    println("mod")
    //  if (validMessage) {
    //  println("set canvas data")
    //  val uint = me.data.cast[js.Array[Int]]
   //   val byteArray = new Uint8Array(uint) //me.data.cast[dom.ArrayBuffer])
      setCanvasDataFromBytes(byteArray1)
  //  }
//    validMessage
  }
}
