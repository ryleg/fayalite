package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas

import rx._

object Pos {

  def apply(
             x: Double, y: Double, dx: Double, dy: Double
             ) = new Pos(Var(x), Var(y), Var(dx), Var(dy))

}

class Pos(
          val x: Var[Double],
          val y: Var[Double],
          val dx: Var[Double],
          val dy: Var[Double]
           ) {
  def clearAll() = {
    Canvas.ctxR().clearRect( //rekt
      x(),
      y(),
      dx(),
      dy()
    )
  }
}
