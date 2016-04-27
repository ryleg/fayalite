package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.{CanvasContextInfo, LatCoord}
import org.fayalite.sjs.canvas.CanvasBootstrap._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import rx.ops.{DomScheduler, Timer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.collection.mutable
import scala.util.Try


trait TileCoordinator {

  val absoluteLatResolve = mutable.Map[LatCoord, CanvasContextInfo]()

  /**
    *
    */
  val indexLatResolve = mutable.Map[LatCoord, CanvasContextInfo]()

  /**
    * It's easier to just re-use tiles than modify the DOM
    */
  val spareTiles = mutable.Queue[CanvasContextInfo]()

  /**
    * Last click element, a single canvas that moves around
    * representing location of last clicked element
    */
  val mLast =
    createCanvasZeroSquare(minSize, commentGreen, 0.1D)


  val bLast =
    createCanvasZeroSquare(bulkSize, annotationYellow, 0.03D)

  val bHover =
    createCanvasZeroSquare(bulkSize, keywordOrange, 0.03D)

  val mHover =
    createCanvasZeroSquare(minSize, methodGold, .1D)

  def reactIsValModifier(c: String, t: CanvasContextInfo) = {
    if (c == "l" &&
      indexLatResolve.get(t.latCoords.left)
        .exists {
          _.text.exists {
            _ == "a"
          }
        } &&
      indexLatResolve.get(t.latCoords.left.left)
        .exists {
          _.text.exists {
            _ == "v"
          }
        } &&
      indexLatResolve.get(t.latCoords.left.left.left).isEmpty
    ) {
      Seq(
        t,
        indexLatResolve.get(t.latCoords.left).get,
        indexLatResolve.get(t.latCoords.left.left).get
      ).foreach {
        z =>
          z.context.clearRect(0D, 0D, z.tileSize, z.tileSize)
          z.drawText(z.text.get, keywordOrange)
      }
    }
  }


}

/**
  * Setup listeners for inputs from client
  * in terms of mouse / key actions
  */
object InputBootstrap extends InputHelp
with TileCoordinator {

  // For any future / thread related stuff use this
  implicit val scheduler = new DomScheduler()

  //Unused temporarily
  def processTileMatrix(tm: Array[Array[CanvasContextInfo]]) = {}

  // Primary flash rate for the cursor
  val heartBeat = Timer(1400.millis)

  def init() : Unit = {
    //disableRightClick()
    println("Input bootstrap")
    //mkMinTile("AD")

    window.onkeyup = (ke: KeyboardEvent) => {

    }

    def mkMinTile(c: String) = {
      val t = createCanvasZeroSquare(
        minSize, alpha=0D, zIndex=10
      ).copy(text = Some(c))
      t.moveTo(mLast)
      println("Made tile ", t.absoluteCoords)
      absoluteLatResolve(t.absoluteCoords) = t
      indexLatResolve(t.latCoords) = t
      Try{t.drawText(c)}

      reactIsValModifier(c, t)

      t
    }

    def handleBackspace(ke: KeyboardEvent) = {
      ke.preventDefault()
      val k = mLast.absoluteCoords.fromAbsolute.left.toAbsolute
      absoluteLatResolve.get(k).foreach{
        q =>
          println("found tilemap ")
          absoluteLatResolve.remove(k)
          indexLatResolve.remove(mLast.latCoords)
          q.turnOff()
          spareTiles += q
      }
      mLast.shiftLeftCarriage()
    }

    document.onkeydown = (ke: KeyboardEvent) => {
      val numShifts = if (ke.ctrlKey) {
        if (ke.altKey) 3 else 2
      } else 1

      ke.keyCode match {
        case KeyCode.backspace =>
          handleBackspace(ke)
        case KeyCode.left =>
          (0 until numShifts).foreach{_ => mLast.shiftLeft()}
        case KeyCode.right =>
          (0 until numShifts).foreach{_ =>  mLast.shiftRight()}
        case KeyCode.up =>
          (0 until numShifts).foreach{_ =>   mLast.shiftUp()}
        case KeyCode.down =>
          (0 until numShifts).foreach{_ =>  mLast.shiftDown()}
        case KeyCode.tab =>
          ke.preventDefault()
          mLast.shiftHorizontal(4)
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
