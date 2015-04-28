package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas

import rx._
import rx.ops._

object ArbitraryCursor {
  
}

// TODO : Switch to rectangle class.

/**
 * Horizontal or vertical is really all that arbitrary means.
 * @param pos : Rectangle that gets colored.
 */
class ArbitraryCursor(val pos: Rx[Pos]) extends Element with Flasher {

  flash() = true

  val deltaPos = pos.reduce{
    (posA : Pos, posB : Pos) =>
      val p = posA
      posA.clearAll()
      draw()
      posB
  }

  def draw() : Unit = {
    val p = pos()
    Canvas.ctxR().fillStyle = "#FFC66D"
    Canvas.ctxR().globalAlpha = .7
    Canvas.ctxR().fillRect(p.x(),p.y(), p.dx(), p.dy())
  }

  def clear() : Unit = {
    val p = pos()
    Canvas.ctxR().clearRect(p.x(),p.y(), p.dx(), p.dy())
  }


}
