package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.canvas.Schema.Position
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSStringOps
import Schema._

import PositionHelpers._

import scala.util.Try


object Text {

  val textColor = "#A9B7C6"

}

import Text._
import rx.ops._

import PositionHelpers._

/**
 * A single interactable container for UI elements that fit neatly into a box.
 * @param xyi: Lattice offset, grid element 5,0 for instance, not pixels.
 * @param extraBuffer : Interior pixel offset from grid lines in all directions.
 * @param offset : Double based offset from bottom left buffered from gridline.
 * @param grid : Measurement system this element is based on.
 */
abstract class GridElement(
                            val xyi: Var[LatCoord] = l0,
                            val extraBuffer: Int = 0,
                            val offset: Var[LatCoordD] = ld0//,
                            )(implicit grid: Grid) extends Drawable {
  import grid._
  import gridTranslator._

  val area = spacing.map{_.-(LatCoordD(extraBuffer.toInt, extraBuffer.toInt))}

  val topLeftB = xyi map {
    q => q : LatCoordD
  } // map {_ - bufferLC()}

  val gridCenterArea = topLeftB.map{
    o =>
      LatCoord2D(o, area())
  }

  override val pos = gridCenterArea

  val deltaPos = pos.reduce{
    (posA : LC2D, posB : LC2D) =>
      println("pos reduce " + posA.str + " " + posB.str)
      val p = posA
      posA.clearAll()
      drawActual()
      posB
  }

  def clear() : Unit = {
    pos().clearAll()
  }

}

class Symbol(
            val char: Var[Char], // change to styled text to absorb styling monad ops
            val latticeCoordinates : Var[LatCoord]
            )(implicit grid: Grid) extends GridElement(
  latticeCoordinates,
  extraBuffer = 0,
  offset = Var { LatCoordD(0D, 0D) }
) {
  import grid._
  import gridTranslator._

 // char.foreach{_ => redraw()}

  def draw() = {
    val c = char()
    val coords = xyi() + LatCoord(0,1) : LatCoordD
    println("draw coords " + coords.x + " " + coords.y)
    ctx.fillText(c.toString,
      coords.x, coords.y)//bottomLeftBufferedWithOffset().x,
      //bottomLeftBufferedWithOffset().y)
    style{
      pos().fillRect()
    }(CanvasStyling(globalAlpha = .5, fillStyle="red"))
  }
}
