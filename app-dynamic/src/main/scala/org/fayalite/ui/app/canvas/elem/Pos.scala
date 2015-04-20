package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas

import rx._

object Pos {



}

case class Pos(x: Var[Double], y: Var[Double], dx: Var[Double], dy: Var[Double]) {
  def clearAll() = {
    Canvas.ctxR().clearRect(//rekt
      x(),
      y(),
      dx(),
      dy()
    )
  }
}
