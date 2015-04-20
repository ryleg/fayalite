package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Input.Mouse
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}
import rx._

object Grid {

  def apply() = new Grid()
  implicit def intToVarInt(int: Double) : Var[Double] = Var(int)


}
import Grid._

object PositionHelpers {
  def apply(x: Double, y: Double) = XY(Var(x), Var(y))
  case class XY(x: Var[Double], y: Var[Double])

  case class SpacedPosition(pos: Pos, spacing: XY)

}

class Grid(
            val pos: Pos = Pos(Var(0D), Var(0D), Canvas.widthR, Canvas.heightR),
            val spacingX: Var[Double] = Var(15D),
            val spacingY: Var[Double] = Var(15D)
            )
extends Element with Drawable
{


  val cellXY = Rx {
    val me = Mouse.move()
    if (me == null) (0, 0) else {
      val x = me.clientX
      val y = me.clientY
      println("cleint xy " + x +  " " + y )
      val cellX = (x / spacingX()).toInt
      val cellY = (y / spacingY()).toInt
      println(s"cellxy $cellX $cellY}")
      val xO = cellX*spacingX()
      val yO = cellY*spacingY()
      Canvas.ctxR().fillStyle = "#FFC66D"
      Canvas.ctxR().globalAlpha = .2
      Canvas.ctxR().fillRect(xO+1, yO-1, 10, 1)

    }
  }

  val numColumns = Rx {
    (pos.dx() / spacingX()).toInt
  }

  val numRows = Rx {
    (pos.dy() / spacingY()).toInt
  }

  redraw()

  def clear() : Unit = {}
  def draw() : Unit = {
    Schema.TryPrintOpt { println(numColumns() + " " + numRows() + " wtf")}
    println("draw called ")
    Canvas.ctxR().fillStyle = "#6897BB"
    Canvas.ctxR().globalAlpha = .2
    for (row <- 0 until numRows()) {
      Canvas.ctxR().fillRect(0, row*spacingX(), pos.dx(), 1)
    }
    for (col <- 0 until numColumns()) {
      Canvas.ctxR().fillRect(col*spacingY(), 0, 1, pos.dy())
    }



  }
}