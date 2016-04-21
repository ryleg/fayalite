package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.CanvasContextInfo
import org.fayalite.sjs.canvas.CanvasBootstrap
import org.fayalite.sjs.canvas.CanvasBootstrap._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import rx.core.Obs
import rx.ops.{DomScheduler, Timer}

/**
  * Setup listeners for inputs from client
  * in terms of mouse / key actions
  */
object InputBootstrap extends InputHelp {

  def processTileMatrix(tm: Array[Array[CanvasContextInfo]]) = {

  }


  /**
    * Prevents browser specific right click context
    * menu popup. For custom rendering by canvas
    * of right click handles
    */
  def disableRightClick(): Unit = {
    window.oncontextmenu = (me: MouseEvent) => {
      me.preventDefault()
    }
  }


  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val scheduler = new DomScheduler()

  val heartBeat = Timer(600.millis)


  def init() : Unit = {
    //disableRightClick()
    println("Input bootstrap")

    window.onkeydown = (ke: KeyboardEvent) => {

    }
    window.onkeyup = (ke: KeyboardEvent) => {

    }


    val minTileWidth = tileXWidth / 5

    val minTileLastClickElement =
      createCanvasZeroSquare(60, commentGreen, 0.1D)

    val bulkTileLastClickElement =
      createCanvasZeroSquare(300, annotationYellow, 0.03D)

    val bulkTileHoverElement =
      createCanvasZeroSquare(300, burntGold, 0.03D)

    val minTileHoverElement =
      createCanvasZeroSquare(60, methodGold, .1D)

    heartBeat.foreach{
      _ =>
        minTileLastClickElement.onOff()
    }

    window.onmousedown = (me: MouseEvent) => {
      val minXY = me.tileCoordinates(60)
      val bulXY = me.tileCoordinates(300)
      minTileLastClickElement.move(minXY)
      bulkTileLastClickElement.move(bulXY)
    }

    window.onmousemove = (me: MouseEvent) => {
      //println("ON Mouse move")
        minTileHoverElement.move(me.tileCoordinates(60))
        bulkTileHoverElement.move(me.tileCoordinates(300))
      }
    }
}
