package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Input.{Key, Mouse}
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}

import rx.ops._
import rx._

import scala.util.{Failure, Success}

object Grid {

  def apply() = new Grid()

  // doesnt seem to work?
  implicit def intToVarInt(db: Double) : Var[Double] = Var(db)


}
import Grid._
import PositionHelpers._


class Grid(
     //       override val pos: Pos = new Pos(Var(0D), Var(0D), Canvas.widthR, Canvas.heightR),
            val spacingX: Var[Double] = Var(16D), // Change to spacingXY
            val spacingY: Var[Double] = Var(16D),
            val elementBuffer: Var[Int] = Var(1)
            )(implicit cs : CanvasStyling = CanvasStyling(fillStyle="#6897BB",globalAlpha = .8))
extends Element with Drawable
{
  implicit val grid_ = this

  pos().dx() = Canvas.widthR()
  pos().dy() = Canvas.heightR()

  implicit def latCoordTransl(latCoord: Rx[LatCoord]): Rx[LatCoordD] = {
    latCoord.map{
      lc => LatCoordD(lc.x*spacingX(), lc.y*spacingY())
    }
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslT(latCoord: Rx[LatCoordD]): Rx[LatCoord] = {
    latCoord.map{
      lc => LatCoord((pos().dx()/lc.x).toInt, (pos().dy()/lc.x()).toInt)
    }
  }

  /**
   * Find nearest line between characters.
   */
  val cursorXY = Mouse.click.map{
    c =>
      val x = c.x()
      val y = c.y()
      val cellX = ((x-spacingX()/2) / spacingX()).toInt + 1
      val cellY = (y / spacingY()).toInt
      xyi(cellX, cellY)
/*      val xO = cellX*spacingX() - 1
      val yO = cellY*spacingY()
      Pos(xO, yO, 1, spacingY() - 1)*/
  }

 /* val cursor = new GridRectFlash(cursorXY)

  val hoverCoords = Rx {
    val xO = xActive.map{x => spacingX()*x + 1}()
    val yO = yActive.map{y => spacingY()*y - 1}()
    Pos(xO, yO, spacingX.map{s => s - 1}(), 1D)
  }

*/
  /*
  scala.util.Try {
    val hoverCursor = new GridRectFlash(xyi(5, 5), xy(), xy(spacingX() - 1, 1D))
  } match {
    case Success(x) => println("made hover")
    case Failure(e) => e.printStackTrace(); println("hover failed")
  }*/
/*

  val hoverCoords = Rx {
   val cxy = cellXY()

 }*/

  /**
   * Find what cell mouse is currently on.
   */
  val cellXY = Rx {
    val (x, y) = Mouse.move()
      val cellX = (x / spacingX()).toInt
      val cellY = (y / spacingY()).toInt + 1
      xyi(cellX, cellY)
    /*  // xActive.update(cellX)
      if (xActive() != cellX) xActive() = cellX
      if (yActive() != cellY) yActive() = cellY
      val xO = cellX*spacingX()
      val yO = cellY*spacingY()*/
  }

  val numColumns = Rx {
    (pos().dx() / spacingX()).toInt + 1
  }

  val numRows = Rx {
    (pos().dy() / spacingY()).toInt + 1
  }

  // TODO : Change to Pos based clear
  def clear() : Unit = {}
  def draw() : Unit = {
    // TODO : Make pretty
    for (row <- 0 until numRows()) {
      Canvas.ctxR().fillRect(0, row*spacingX(), pos().dx(), 1)
    }
    for (col <- 0 until numColumns()) {
      Canvas.ctxR().fillRect(col*spacingY(), 0, 1, pos().dy())
    }
  }

  redraw()

}