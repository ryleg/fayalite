package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import rx._


trait Drawable {

  def clear() : Unit
  def draw() : Unit
  def redraw() : Unit = { clear() ; draw() }

  val resize = Obs(Canvas.onresize, skipInitial = true) {
    redraw()
  }

}
