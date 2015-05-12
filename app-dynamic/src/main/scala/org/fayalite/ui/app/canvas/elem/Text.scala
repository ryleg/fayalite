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

trait Shiftable {
  implicit val grid: Grid
  val latCoord: Var[LatCoord]
  import grid.gridTranslator._
  def lc = latCoord()
  def shiftRight: Boolean = {
      val tooFar = lc.right.x >= grid.cols - 1
      latCoord() = if (tooFar) {
        lc.left0.down
      } else lc.right
      tooFar
  }
}

/**
 * A single interactable container for UI elements that fit neatly into a box.
 * @param latCoord: Lattice offset, grid element 5,0 for instance, not pixels.
 * @param extraBuffer : Interior pixel offset from grid lines in all directions.
 * @param offset : Double based offset from bottom left buffered from gridline.
 * @param grid : Measurement system this element is based on.
 */
abstract class GridElement(
                            val latCoord: Var[LatCoord] = l0,
                            val extraBuffer: Int = 0,
                            val offset: Var[LatCoordD] = ld0,
                            val area: Option[Var[LatCoordD]] = None
                            )(implicit val grid: Grid, val canvasStyling: CanvasStyling =
  CanvasStyling()) extends Drawable
  with Shiftable
{
  import grid._
  import gridTranslator._
  implicit val canvasStylingProper = canvasStyling

  val areaActual = spacing.map{_.-(extraBuffer)}

  val usedArea = area.map{_.map{q => q}}.getOrElse(areaActual)

  val latCoordD = latCoord.map{q => q : LCD}
  
  val topLeftB = latCoord map {
    q => q : LatCoordD
  } // map {_ - extraBuffer}

  override val pos = topLeftB.map{
    o => LatCoord2D(o+offset(), usedArea())
  }

  val deltaPos = pos.reduce{
    (posA : LC2D, posB : LC2D) =>
  //    println("pos reduce " + posA.str + " " + posB.str)
      val p = posA
      posA.clearAll()
      drawActual()
      posB
  }

  def clear() : Unit = {
    pos().clearAll()
  }

  redraw()

}

/*
Should be able to draw a secondary grid
over the primary and build N levels of
2Chirals or MChirals.
 */

class Symbol(
            val char: Var[Char], // change to styled text to absorb styling monad ops
            val latticeCoordinates : Var[LatCoord]
            )(override implicit val grid: Grid) extends GridElement(
  latticeCoordinates,
  extraBuffer = 0,
  offset = Var { LatCoordD(0D, 0D) }
)(grid=grid) {
  import grid._
  import gridTranslator._

  char.foreach{_ => redraw()}

  def draw() = {
    val c = char()
    val coords = latCoord() + LatCoord(0,1) : LatCoordD
   // println("draw coords " + coords.x + " " + coords.y)
    ctx.fillText(c.toString,
      coords.x, coords.y)//bottomLeftBufferedWithOffset().x,
      //bottomLeftBufferedWithOffset().y)
/*    style{
      pos().fillRect()
    }(CanvasStyling(globalAlpha = .5, fillStyle="red"))*/
  }
}
/*


val srC4 = runSearch(4, 300)


 */
