package org.fayalite.ui.app.canvas.elem

import rx._


trait PositionHelpers {

  def xy(x: Double = 0D, y: Double = 0D): XY = XY(Var(x), Var(y))
  def xyi(x: Int = 0, y: Int = 0): XYI = XYI(Var(x), Var(y))

  case class XY(x: Var[Double], y: Var[Double]) {
    def plus(other: XY) = {
      this.copy(
        x=Var(x() + other.x()), y=Var(y() + other.y())
      )
    }
  }
  case class SpacedPosition(pos: Pos, spacing: XY)

  val lc0 = LatCoord(0, 0)
  val lcd0 = LatCoordD(0D, 0D)
  val l0 = Rx{LatCoord(0, 0)}
  val ld0 = Rx{LatCoordD(0D, 0D)}
  val l20 = Rx{LatCoord2(LatCoord(0, 0),LatCoord(0, 0))}
  val ld20 = Rx{LatCoord2D(lcd0, lcd0)}

  case class LatCoord(x: Int, y: Int)
  case class LatCoordD(x: Double, y: Double)
  case class LatCoord2(xy: LatCoord, xy2: LatCoord)
  case class LatCoord2D(xy: LatCoordD, xy2: LatCoordD)

  case class XYI(x: Var[Int], y: Var[Int]) {
    def plus(other: XYI) = {
      this.copy(
        x=Var(x() + other.x()), y=Var(y() + other.y())
      )
    }
  }

}

object PositionHelpers extends PositionHelpers
