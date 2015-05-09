package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Input.{Key, Mouse}
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}

import rx.ops._
import rx._

import scala.util.{Try, Failure, Success}

object Grid {

  def apply() = new Grid()

  // doesnt seem to work?
  implicit def intToVarInt(db: Double) : Var[Double] = Var(db)


}
import Grid._
import PositionHelpers._


class GridTranslator(pos: Rx[LatCoord2D],
                     spacing: Rx[LatCoordD]
                      ) {
  implicit def latCoordTranslNR(lc: LatCoord): LatCoordD = {
    LatCoordD(lc.x*spacing().x, lc.y*spacing().y)
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslTNR(lc: LatCoordD): LatCoord = {
    LatCoord(
      // grid width divBy
      (pos().xy2.x/lc.x).toInt,
      (pos().xy2.y/lc.y()).toInt)
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

class Grid(
            //       override val pos: Pos = new Pos(Var(0D), Var(0D), Canvas.widthR, Canvas.heightR),
            val spacingX: Var[Double] = Var(16D), // Change to spacingXY
            val spacingY: Var[Double] = Var(16D),
            val elementBuffer: Var[Int] = Var(1)
            )(implicit cs : CanvasStyling = CanvasStyling(
  fillStyle="#6897BB",
  globalAlpha = .8)
            )
  extends Element with Drawable
{

  val spacing = Rx{LatCoordD(spacingX(), spacingY())}

  override val pos = ld20.map{
    _.copy(xy2=LatCoordD(Canvas.heightR(), Canvas.widthR()))
  }

  val gridTranslator = new GridTranslator(pos, spacing)
  import gridTranslator._

  implicit val grid_ = this
  val cursorDxDy =Var(
    spacing.map{_.-(1)}())

  /**
   * Find nearest line between characters.
   */
  val cursorXY = Mouse.click.map{c =>
    //cursor.xyi() =
/*      style {
        c.fillRect(xy(20D, 20D))
      }*/
      c: LatCoord

  }

/*
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

    val mev = Mouse.move()
 //z   Canvas.ctxR().fillStyle = "red"
   // Canvas.ctxR().fillRect(mev.x, mev.y, 10, 10)

     val lmve = mev : LatCoord
    Canvas.ctxR().fillStyle = "yellow"
    Canvas.ctxR().fillRect(lmve.x, lmve.y, 10, 10)

    /*  // xActive.update(cellX)
      if (xActive() != cellX) xActive() = cellX
      if (yActive() != cellY) yActive() = cellY
      val xO = cellX*spacingX()
      val yO = cellY*spacingY()*/
  }

/*  val cursor = Try { new GridRect(flashing=false, dxDy = cursorDxDy)
  }
  cursor match {
    case Failure(e) => e.printStackTrace(); case Success(x) => x
  }

  cellXY.foreach{q =>
    println("cellXY Obs: " + q.str)
    cursor.toOption.get.xyi() = q
  }*/

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