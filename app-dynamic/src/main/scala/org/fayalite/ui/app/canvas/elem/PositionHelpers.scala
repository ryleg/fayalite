package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import rx._

import scala.util.Try


object PositionHelpers {


/*
// This kills rx.ops._ import carefully. Or make a nested class.
  implicit class RxOps[T](rxx: Rx[T]) {
    def reset(f: => T) = {
      rxx.parents.map{q => Try{q.asInstanceOf[Var[T]]() = f}}
    }
  }
*/

  def xy(x: Double = 0D, y: Double = 0D): LatCoordD = LatCoordD(x,y)
  def xyi(x: Int = 0, y: Int = 0): LatCoord = LatCoord(x,y)
  
  case class SpacedPosition(pos: Pos, spacing: LatCoordD)

  def lc0 = LatCoord(0, 0)
  def lcd0 = LatCoordD(0D, 0D)
  def l0 = Var{LatCoord(0, 0)}
  def ld0 = Var{LatCoordD(0D, 0D)}
  def l20 = Var{LatCoord2(LatCoord(0, 0),LatCoord(0, 0))}
  def ld20 = Var{LatCoord2D(lcd0, lcd0)}

  type LC = LatCoord
  type LCD = LatCoordD
  type LC2 = LatCoord2
  type LC2D = LatCoord2D
  type VL = Var[LatCoord]
  type VLD = Var[LatCoordD]
  type VL2 = Var[LatCoord2]
  type VL2D = Var[LatCoord2D]

  case class LatCoord(x: Int, y: Int) {
    def *(o: Int) = {
      this.copy(x*o, y*o)
    }
    def +(o: LatCoord) = this.copy(o.x+x, o.y+y)
    def str = s"x:$x,y:$y"
  }
  case class LatCoordD(x: Double, y: Double) {
    def +(other: LCD) = {
      this.copy(other.x+x, other.y+y)
    }
    def -(o: LCD) = {
      this.copy(x-o.x, y-o.y)
    }
    def -(o: LC) = this.copy(x-o.x, y-o.y)
    def -(oi: Int) = {
      this.copy(x-oi, y-oi)
    }
    def fillRect(dxDy: LatCoordD) = {
      LatCoord2D(this, dxDy).fillRect()
    }
    def str = s"x:$x,y:$y"
  }
  case class LatCoord2(xy: LatCoord, xy2: LatCoord) {
    def str = xy.str + "|" + xy2.str
  }

  case class LatCoord2D(xy: LatCoordD, xy2: LatCoordD) {
    def str = xy.str + "|" + xy2.str
    def x = xy.x
    def y = xy.y
    def dx = xy2.x
    def dy = xy2.y
    def plus1(other: LCD) = {
      this.copy(xy = other.+(this.xy))
    }
    // Change to import something._ ; clearRect(pos)
    def clearAll() = {
      Canvas.ctxR().clearRect(//rekt
        x,
        y,
        dx,
        dy
      )
    }

    def fillRect() = {
      Canvas.ctxR().fillRect(
        x,
        y,
        dx,
        dy
      )
    }
  }

  case class XYI(x: Var[Int], y: Var[Int]) {
    def plus(other: XYI) = {
      this.copy(
        x=Var(x() + other.x()), y=Var(y() + other.y())
      )
    }
  }

}

trait PositionHelpers {

}
