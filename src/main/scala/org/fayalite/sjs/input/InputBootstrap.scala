package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.{CanvasContextInfo, LatCoord}
import org.fayalite.sjs.canvas.CanvasBootstrap
import org.fayalite.sjs.canvas.CanvasBootstrap._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import rx.core.Obs
import rx.ops.{DomScheduler, Timer}

import scala.collection.mutable
import scala.util.Try

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

  val tileMap = mutable.Map[LatCoord, CanvasContextInfo]()

  val spareTiles = mutable.Queue[CanvasContextInfo]()

  def init() : Unit = {
    //disableRightClick()
    println("Input bootstrap")
    //mkMinTile("AD")

    window.onkeyup = (ke: KeyboardEvent) => {

    }

    val mLast =
      createCanvasZeroSquare(minSize, commentGreen, 0.1D)

    val bLast =
      createCanvasZeroSquare(bulkSize, annotationYellow, 0.03D)

    val bHover =
      createCanvasZeroSquare(bulkSize, burntGold, 0.03D)

    val mHover =
      createCanvasZeroSquare(minSize, methodGold, .1D)

    def mkMinTile(c: String) = {
      val t = createCanvasZeroSquare(minSize, alpha=0D, zIndex=10)
      t.moveTo(mLast)
      println("Made tile ", t.absoluteCoords)
      tileMap(t.absoluteCoords) = t
      Try{t.drawText(c)}
      t
    }

    def handleBackspace(ke: KeyboardEvent) = {
      ke.preventDefault()
      val k = mLast.absoluteCoords.fromAbsolute.left.toAbsolute
      tileMap.get(k).foreach{
        q =>
          println("found tilemap ")
          tileMap.remove(k)
          q.turnOff()
          spareTiles += q
      }
      mLast.shiftLeftCarriage()
    }

    document.onkeydown = (ke: KeyboardEvent) => {
      ke.keyCode match {
        case KeyCode.backspace =>
          handleBackspace(ke)
        case KeyCode.left =>
          mLast.shiftLeft()
        case KeyCode.right =>
          mLast.shiftRight()
        case KeyCode.up =>
          mLast.shiftUp()
        case KeyCode.down =>
          mLast.shiftDown()

        case _ =>
      }
    }

    document.onkeypress = (ke: KeyboardEvent) => {
      val chr = ke.keyString
      println("Key down " + chr)
      ke.keyCode match {
        case KeyCode.enter =>
          mLast.shiftDownLeftZero(minSize)
        case KeyCode.backspace =>
          handleBackspace(ke)
        case kc =>
      }
      mkMinTile(chr)
      mLast.shiftRight()
    }
    heartBeat.foreach{
      _ =>
        mLast.onOff()
    }

    window.onmousedown = (me: MouseEvent) => {
      val minXY = me.tileCoordinates(minSize)
      val bulXY = me.tileCoordinates(bulkSize)
      mLast.move(minXY)
      bLast.move(bulXY)
    }

    window.onmousemove = (me: MouseEvent) => {
      //println("ON Mouse move")
        mHover.move(me.tileCoordinates(minSize))
        bHover.move(me.tileCoordinates(bulkSize))
      }
    }
}
