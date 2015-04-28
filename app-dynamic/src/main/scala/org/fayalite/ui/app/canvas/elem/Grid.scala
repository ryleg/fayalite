package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Input.{Key, Mouse}
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}

import rx.ops._
import rx._

object Grid {

  def apply() = new Grid()

  // doesnt seem to work?
  implicit def intToVarInt(db: Double) : Var[Double] = Var(db)


}
import Grid._

class Grid(
            val pos: Pos = new Pos(Var(0D), Var(0D), Canvas.widthR, Canvas.heightR),
            val spacingX: Var[Double] = Var(16D),
            val spacingY: Var[Double] = Var(16D)
            )
extends Element with Drawable
{

  val elementBuffer = Var(1)

  val xActive = Var(0)
  val yActive = Var(0)

  //Key.Arrow.

  val cursorXY = Mouse.click.map{
    c =>
      val x = c.x()
      val y = c.y()
      val cellX = ((x-spacingX()/2) / spacingX()).toInt + 1
      val cellY = (y / spacingY()).toInt
      val xO = cellX*spacingX() - 1
      val yO = cellY*spacingY()
      Pos(xO, yO, 1, spacingY() - 1)
  }

  val cursor = new ArbitraryCursor(cursorXY)

  val hoverCoords = Rx {
    val xO = xActive.map{x => spacingX()*x + 1}()
    val yO = yActive.map{y => spacingY()*y - 1}()
    Pos(xO, yO, spacingX.map{s => s - 1}(), 1D)
  }

  val hoverCursor = new ArbitraryCursor(hoverCoords)

  val cellXY = Rx {
    val (x, y) = Mouse.move()
      val cellX = (x / spacingX()).toInt
      val cellY = (y / spacingY()).toInt + 1
      // xActive.update(cellX)
      if (xActive() != cellX) xActive() = cellX
      if (yActive() != cellY) yActive() = cellY
      val xO = cellX*spacingX()
      val yO = cellY*spacingY()
     // Canvas.ctxR().fillStyle = "red"
    //  Canvas.ctxR().globalAlpha = .7
    //  Canvas.ctxR().fillRect(xO+1, yO-1, spacingX()-1, 1)
  }

  val numColumns = Rx {
    (pos.dx() / spacingX()).toInt + 1
  }

  val numRows = Rx {
    (pos.dy() / spacingY()).toInt + 1
  }

  redraw()

  // TODO : Change to Pos based clear
  def clear() : Unit = {}
  def draw() : Unit = {
   // Schema.TryPrintOpt { println(numColumns() + " " + numRows() + " wtf")}
    //println("draw called ")
    Canvas.ctxR().fillStyle = "#6897BB"
    Canvas.ctxR().globalAlpha = .8
    for (row <- 0 until numRows()) {
      Canvas.ctxR().fillRect(0, row*spacingX(), pos.dx(), 1)
    }
    for (col <- 0 until numColumns()) {
      Canvas.ctxR().fillRect(col*spacingY(), 0, 1, pos.dy())
    }



  }
}