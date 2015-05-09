package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import rx._
import PositionHelpers._

import scala.util.Try

object Drawable {

  case class CanvasStyling(
                          font: String = "13pt monospace",
                          fillStyle: String =  "#A9B7C6",
                          globalAlpha: Double = 1D
                            )

}

import rx.ops._

trait Drawable {

  def clear() : Unit
  def draw() : Unit
//  def drawActual(): Unit = if (visible()) style{draw()}
  def drawActual(): Unit = style{draw()}

  def redraw() : Unit = { clear() ; drawActual() }

  val visible : Rx[Boolean] = Rx{true}

 // visible.foreach{q => if (q) redraw() else clear()}

  val pos : Rx[LC2D] = ld20

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
    val prevAlpha = ctx.globalAlpha
    ctx.font = stylingEvidence.font
    ctx.fillStyle = stylingEvidence.fillStyle
    ctx.globalAlpha = stylingEvidence.globalAlpha
    val ret = f
    ctx.font = prevFont
    ctx.fillStyle = prevFillStyle
    ctx.globalAlpha = prevAlpha
    ret
  }

}
