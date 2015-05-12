package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.App
import org.fayalite.ui.app.canvas.Input.{Key, Mouse}
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import org.fayalite.ui.app.canvas.elem.PositionHelpers.{LatCoord, LatCoordD}
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}

import rx.ops._
import rx._

import scala.util.{Try, Failure, Success}

object Grid {

  def apply() = new Grid()

  // doesnt seem to work?
  implicit def intToVarInt(db: Double) : Var[Double] = Var(db)

  case class ChiralCell(
                       side: Either[LatCoord, LatCoord]
                         ) {
    val offset = side match {
      case Left(lc) => lc
      case Right(lc) => lc.copy(x=lc.x+1)
    }
  }

}
import Grid._
import PositionHelpers._


class GridTranslator(pos: Rx[LatCoord2D],
                     spacing: Rx[LatCoordD]
                      ) {

  val numColumns = Rx {
    (pos().dx() / spacing().x).toInt + 1
  }

  val numRows = Rx {
    (pos().dy() / spacing().x).toInt + 1
  }

  implicit def lcChiral(lc: LatCoordD): ChiralCell = {
    val lct = lc : LatCoord
    val lcti = lct : LatCoordD
    val cellMiddle = lcti.x + spacing().x/2
    ChiralCell{if (lc.x < cellMiddle) Left(lct) else Right(lct)}
  }

  implicit def latCoordTranslNR(lc: LatCoord): LatCoordD = {
    LatCoordD(lc.x*spacing().x, lc.y*spacing().y)
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslTNR(lc: LatCoordD): LatCoord = {
    def sx = spacing().x
    def sy = spacing().y
    val fx = Math.floor(lc.x/sx).toInt
    val fy = Math.floor(lc.y/sy).toInt
    LatCoord(fx, fy)
  }
  // TODO: Move to translator trait and add implicit grid param
  implicit def latCoordTransl(latCoord: Rx[LatCoord]): Rx[LatCoordD] = {
    latCoord.map{
      lc => lc : LatCoordD
    }
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslT(latCoord: Rx[LatCoordD]): Rx[LatCoord] = {
    latCoord.map{
      lc => lc : LatCoord
    }
  }
}
import Canvas.{widthR=>width,heightR=>height}

class Grid(
            val spacing: Var[PositionHelpers.LatCoordD] =
  Var(PositionHelpers.LatCoordD(16D, 16D)),
            val elementBuffer: Var[Int] = Var(1)
            )(implicit cs : CanvasStyling = CanvasStyling(
  fillStyle="#6897BB",
  globalAlpha = .8)
            )
  extends Element with Drawable
{
  override val pos = ld20.map{
    _.copy(xy2=LatCoordD(Canvas.heightR(), Canvas.widthR()))}
  val gridTranslator = new GridTranslator(pos, spacing)
  import gridTranslator._
  implicit val grid_ = this

  def cols = gridTranslator.numColumns()
  def rows = gridTranslator.numRows()

  val cursorDxDy =Var(spacing.map{_.-(1)}())

  /**
   * Find nearest line between characters by cell midpoint.
   */
  val cursorXY = Mouse.click.map{c => c: ChiralCell}

  val cursor = new GridRect(
    dxDy=Var(LatCoordD(2, spacing().y-2)),
    offset = Var(LatCoordD(1D, 1D))
  )

    val tp = LatCoordD(10, 5)

  cursorXY.foreach{
    cc =>
      cursor.latCoord() = cc.offset
     // style{LatCoord2D(cc.offset,
    //    tp).fillRect()}(CanvasStyling(fillStyle = "red"))
  }

  /**
   * Find what cell mouse is currently on.
   */
  val cellXY = Rx {
    Mouse.move() : LC
  }

  val hover = new GridRect(
    dxDy=Var(LatCoordD(spacing().x - 2, 2)),
    offset = Var(LatCoordD(1D, spacing().y-4D))
  )
  cellXY.foreach{ cxy => hover.latCoord() = cxy.copy(x=cxy.x) }

  def clear() : Unit = {} // TODO : Change to Pos based clear
  def draw() : Unit = { // TODO : Make pretty
    for (row <- 0 until numRows()) {
      val lineCoord = LatCoord(0, row) // change to Inv map on next func.
      val lineArea = LatCoordD(pos().dx(), elementBuffer())
      LatCoord2D(lineCoord, lineArea).fillRect()
      //Canvas.ctxR().fillRect(0, row*spacing().x, pos().dx(), elementBuffer())
    }
    for (col <- 0 until numColumns()) {
      val lineCoord = LatCoord(col, 0)
      val lineArea = LatCoordD(elementBuffer(), pos().dx())
      LatCoord2D(lineCoord, lineArea).fillRect()
      //Canvas.ctxR().fillRect(col*spacing().y, 0, elementBuffer(), pos().dy())
    }
  }

  redraw()

}