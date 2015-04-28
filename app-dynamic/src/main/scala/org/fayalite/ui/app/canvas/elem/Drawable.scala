package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import rx._

object Drawable {

  case class CanvasStyling(
                          font: String = "13pt monospace",
                          fillStyle: String =  "#A9B7C6"
                            )

}

trait Drawable {

  def clear() : Unit
  def draw() : Unit
  def redraw() : Unit = { clear() ; draw() }

  val resize = Obs(Canvas.onresize, skipInitial = true) {
    redraw()
  }

  /**
   * Canvas context requires setting flags before any operation, hence this wrapper.
   * @param f: Render func
   * @tparam T: Render return type
   * @return : Result of render func
   */
  def style[T](f : => T)(implicit stylingEvidence: CanvasStyling = CanvasStyling()): T = {
    val prevFont = ctx.font
    val prevFillStyle = ctx.fillStyle
    ctx.font = stylingEvidence.font
    ctx.fillStyle = stylingEvidence.fillStyle
    val ret = f
    ctx.font = prevFont
    ctx.fillStyle = prevFillStyle
    ret
  }

}
