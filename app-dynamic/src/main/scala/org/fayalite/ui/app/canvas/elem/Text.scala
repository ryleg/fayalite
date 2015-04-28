package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.canvas.Schema.Position
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSStringOps
import Schema._

import PositionHelpers._


object Text {

  val textColor = "#A9B7C6"

}

import Text._
import rx.ops._

abstract class GridElement(
                            val xyi: XYI
                            )(implicit grid: Grid) extends Drawable
{
  val gridOffset = Rx {
    xy(xyi.x() * grid.spacingX() + grid.elementBuffer(),
    xyi.y() * grid.spacingY() - grid.elementBuffer())
  }
  val pos = gridOffset.map{
    o =>
      val eb = grid.elementBuffer()*2
      Pos(o.x(), o.y(), grid.spacingX() - eb, grid.spacingY() - eb)
  }
  def clear() : Unit = {
    pos().clearAll()  
  }
  
}


class Symbol(
            val char: Var[Char], // change to styled text to absorb styling monad ops
            val offset : XYI
            )(implicit grid: Grid) extends GridElement(offset) {

  def draw() = {
      val c = char()
        style {
          ctx.fillText(c.toString, gridOffset().x(), gridOffset().y())
        }
    }
}
