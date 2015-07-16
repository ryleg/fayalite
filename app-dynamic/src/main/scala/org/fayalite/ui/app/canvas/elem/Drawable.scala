package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.{PositionHelpers, Canvas}
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import rx._
import PositionHelpers._

import scala.util.Try

object Drawable {

  case class CanvasStyling(
                          font: String = "14pt monospace",
                          fillStyle: String =  "#A9B7C6",
                          globalAlpha: Double = 1D
                            )

}

import rx.ops._

trait Drawable {

  def clear(): Unit

  def draw() : Unit
  def drawActual(): Unit = style{draw()}
  def redraw() : Unit = { clear() ; drawActual() }
  def off = visible() = false
  def on = visible() = true

  val visible : Var[Boolean] = Var{true}

  Obs(visible, skipInitial = true) {
    val q = visible()
    if (q) redraw() else clear()
  }

  val pos : Rx[LC2D] = ld20

  val resize = Obs(Canvas.onresize, skipInitial = true) {
    redraw()
  }

  val styling = CanvasStyling()

  /**
   * Canvas context requires setting flags before any operation, hence this wrapper.
   * @param f: Render func
   * @tparam T: Render return type
   * @return : Result of render func
   */
  def style[T](f : => T)
  //            (implicit stylingEv: CanvasStyling = CanvasStyling())
  : T = {
    val prevFont = ctx.font
    val prevFillStyle = ctx.fillStyle
    val prevAlpha = ctx.globalAlpha
    val stylingEvidence = styling
    // if (styling == CanvasStyling()) stylingEv else styling
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
