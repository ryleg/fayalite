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

  val absoluteLatResolve = mutable.HashMap[LatCoord, CanvasContextInfo]()

  /**
    *
    */
  val indexLatResolve = mutable.HashMap[LatCoord, CanvasContextInfo]()

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

  // TODO : NOT THIS its just an example don't judge.
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

  def handleBackspace(ke: KeyboardEvent) = {
    ke.preventDefault()
    val k = mLast.absoluteCoords.fromAbsolute.left.toAbsolute
    absoluteLatResolve.get(k).foreach{
      q =>
       // println("found tilemap ")
        absoluteLatResolve.remove(k)
        indexLatResolve.remove(mLast.latCoords)
        q.turnOff()
        spareTiles += q
    }
    mLast.shiftLeftCarriage()
  }

  def mkMinTile(c: String) = {
    val t = createCanvasZeroSquare(
      minSize, alpha=0D, zIndex=10
    ).copy(text = Some(c))
    t.moveTo(mLast)
  //  println("Made tile ", t.absoluteCoords)
    absoluteLatResolve(t.absoluteCoords) = t
    indexLatResolve(t.latCoords) = t
    Try{t.drawText(c)}
    reactIsValModifier(c, t)
    t
  }

  def mkCharLikeSquareNode(charLike: String) = {
    val t = createCanvasZeroSquare(
      minSize, alpha=0D, zIndex=10
    ).copy(text = Some(charLike))
    Try{t.drawText(charLike)}
    t
  }

  def updateCoords(t: CanvasContextInfo) = {
    absoluteLatResolve(t.absoluteCoords) = t
    indexLatResolve(t.latCoords) = t
  }

  val wordResolve = mutable.HashMap[LatCoord, Array[CanvasContextInfo]]()
  val wordBubbleResolve = mutable.HashMap[LatCoord, CanvasContextInfo]()

  def drawEllipse(
                   wordLen: Int, origin: LatCoord, bubbleCanvas: CanvasContextInfo
                 ) = {
    val offsetLeft = 1
    val offsetTop =  .5D
    val x = origin.x - (offsetLeft*minSize)
    val y = origin.y - offsetTop*minSize
    // Fix alpha calls to split background filler out, below is hack
    val kappa = .5522848
    val width = (wordLen+2)*minSize
    val height = minSize*2
    val ox = (width / 2) * kappa // control point offset horizontal
    val oy = (height / 2) * kappa // control point offset vertical
    val xe = x + width           // x-end
    val ye = y + height           // y-end
    val xm = x + width / 2      // x-middle
    val ym = y + height / 2   // y-middle
    val ctx = bubbleCanvas.context
    ctx.fillStyle = methodGold
    ctx.strokeStyle = methodGold
    ctx.globalAlpha = .41D
    ctx.lineWidth = 2D
    ctx.moveTo(x, ym)
    ctx.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y)
    ctx.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym)
    ctx.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye)
    ctx.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym)
    //ctx.closePath(); // not used correctly, see comments (use to close off open path)
    ctx.stroke()

  }

  def mkWord(word: String, origin: LatCoord) = {
    val tempCnvRenders = word.map{_.toString}.map{mkCharLikeSquareNode}
    tempCnvRenders.head.move(origin)
    updateCoords(tempCnvRenders.head)
    // Proper way to do this should look like this
    /*    tempCnvRenders.tail.foldRight(tempCnvRenders.head) {
          case (nextElement, foldPrevEl) =>
        }*/
    // Heres the wrong way to do it:
    tempCnvRenders.zipWithIndex.tail.foreach{
      case (c, i) =>
        c.moveTo(tempCnvRenders.head)
        (0 until i).foreach { _ =>
          c.shiftRight()
        }
        updateCoords(c)
    }
    val len = word.length
    val bubbleCanvas = createCanvasZeroSquare(bulkSize*2, zIndex = 2, alpha = 0D)
    drawEllipse(len, origin, bubbleCanvas)

    wordResolve(origin) = tempCnvRenders.toArray
    wordBubbleResolve(origin) = bubbleCanvas
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

  var codeSample = ""

  def init() : Unit = {
    //disableRightClick() // TODO : Enable when scrolling is implemented
    println("Input bootstrap")
    //mkMinTile("AD")

    window.onkeyup = (ke: KeyboardEvent) => {

    }

    document.onkeydown = (ke: KeyboardEvent) => {
      val numShifts = if (ke.ctrlKey) {
        if (ke.altKey) 3 else 2
      } else 1

      ke.keyCode match {
        case KeyCode.a if ke.ctrlKey =>
          println("Cell capture attempt ")
        case KeyCode.backspace =>
          handleBackspace(ke)
        case KeyCode.left =>
          (0 until numShifts).foreach{_ => mLast.shiftLeft()}
        case KeyCode.right =>
          (0 until numShifts).foreach{_ => mLast.shiftRight()}
        case KeyCode.up =>
          (0 until numShifts).foreach{_ => mLast.shiftUp()}
        case KeyCode.down =>
          (0 until numShifts).foreach{_ => mLast.shiftDown()}
        case KeyCode.tab =>
          ke.preventDefault()
          mLast.shiftHorizontal(4)
        case _ =>
      }
    }

    document.onkeypress = (ke: KeyboardEvent) => {
      val chr = ke.keyString
      codeSample += chr
     // println("Key down " + chr)
      ke.keyCode match {
        case KeyCode.enter =>
          mLast.shiftDownLeftZero(minSize)
          println("Code sample " + codeSample)


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

        wordBubbleResolve.get(mHover.location).foreach{
          q =>
            //q.clear()
            println("Hover over word")
        }
      }
    }
}
