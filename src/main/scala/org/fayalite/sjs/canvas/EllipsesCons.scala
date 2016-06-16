package org.fayalite.sjs.canvas

import org.fayalite.sjs.SJSHelp
import org.fayalite.sjs.Schema.{CanvasContextInfo, LatCoord}

import scala.collection.mutable

/**
  * For future non-grid-like bubbles / DAG circles
  */
object EllipsesCons extends CanvasTileUtils with CanvasHelp {
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

  /*

    // When drawing a word:
    drawEllipse(len, origin, bubbleCanvas)
    wordBubbleResolve(origin) = bubbleCanvas

    // When dealing with bubble input.
            wordBubbleResolve.get(mHover.location).foreach{
          q =>
          //  q.clear()
            println("Hover over word")
        }

   */

}
