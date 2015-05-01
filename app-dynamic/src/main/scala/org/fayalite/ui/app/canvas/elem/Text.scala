package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.canvas.Schema.Position
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

trait Movable extends Drawable {

  override val pos : Rx[Pos]

}

/**
 * A single interactable container for UI elements that fit neatly into a box.
 * @param xyi: Lattice offset, grid element 5,0 for instance, not pixels.
 * @param extraBuffer : Interior pixel offset from grid lines in all directions.
 * @param offset : Double based offset from bottom left buffered from gridline.
 * @param dxDy : Fill space of element by default, not strictly required in all cases for
 *             custom elements.
 * @param grid : Measurement system this element is based on.
 */
abstract class GridElement(
                            val xyi: Rx[LatCoord] = l0,
                            val extraBuffer: Int = 0,
                            val offset: Rx[LatCoordD] = ld0,
                            val dxDy: Rx[LatCoordD] = ld0
                            )(implicit grid: Grid) extends Drawable {
/*
  def move(xyiPrime: XYI) = {
    xyi.x() = xyiPrime.x(); xyi.y() = xyiPrime.y()
  }
*/
  import grid._

  val buffer = grid.elementBuffer.map{_ + extraBuffer}
  
  val bottomLeft = xyi.map{ lc =>
    lc.x * grid.spacingX()
    lc.y * grid.spacingY()
  }
  
  val bottomLeftBuffered = Rx {
    val bl = bottomLeft()
    xy(bl.x() + buffer(),
    bl.y() - buffer())
  }

  val bottomLeftBufferedWithOffset = Rx {
    bottomLeftBuffered().plus(offset)
  }

  override val pos : Rx[Pos] = Rx {
    val blbwo = bottomLeftBufferedWithOffset()
    Pos(blbwo.x() + offset.x(), blbwo.y() + offset.y(), dxDy.x(), dxDy.y())
  }

  val deltaPos = Try{ pos.reduce{
    (posA : Pos, posB : Pos) =>
      println("pos reduce " + posA.x())
      val p = posA
      posA.clearAll()
      draw()
      posB
  }}

  val gridCenterArea = bottomLeftBuffered.map{
    o =>
      val eb = buffer()*2
      Pos(o.x(), o.y(), grid.spacingX() - eb, grid.spacingY() - eb)
  }

}


class Symbol(
            val char: Var[Char], // change to styled text to absorb styling monad ops
            val latticeCoordinates : XYI
            )(implicit grid: Grid) extends GridElement(
  latticeCoordinates,
  extraBuffer = 1,
  dxDy = xy(
    grid.spacingX() - 4, -1*(grid.spacingY() - 4)
  )
) {

  def clear() : Unit = {
    gridCenterArea().clearAll()
  }

  def draw() = {
    val c = char()
    ctx.fillText(c.toString, bottomLeftBuffered().x(), bottomLeftBuffered().y())
  }
}
