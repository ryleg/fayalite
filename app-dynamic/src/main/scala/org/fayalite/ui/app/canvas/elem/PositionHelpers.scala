package org.fayalite.ui.app.canvas.elem

import rx._


trait PositionHelpers {

  def xy(x: Double = 0D, y: Double = 0D): XY = XY(Var(x), Var(y))

  case class XY(x: Var[Double], y: Var[Double])
  case class SpacedPosition(pos: Pos, spacing: XY)
  case class XYI(x: Var[Int], y: Var[Int])

}

object PositionHelpers extends PositionHelpers
