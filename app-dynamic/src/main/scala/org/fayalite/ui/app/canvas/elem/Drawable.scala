package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.{PositionHelpers, Canvas}
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import rx._
import PositionHelpers._

import scala.util.Try

/**
  * Contains static references for any
  * usable metadata related to drawing anywhere
  * Either Canvas or otherwise
  */
object Drawable {

  /**
    * This is used because the canvas
    * engine requires setting flags in advance of draw
    * calls, these are the typical modified GUI
    * declarations required most commonly, feel
    * free to add on additional specifications
    * @param font: A string as expected in CSS
    * @param fillStyle : Hex prefixed color code
    * @param globalAlpha : Zero to one float value
    *                    as in png for draw call
    */
  case class CanvasStyling(
                          font: String = "14pt monospace",
                          fillStyle: String =  "#A9B7C6",
                          globalAlpha: Double = 1D
                            )

}

import rx.ops._

/**
  * Something that is gonna get drawn to
  * a screen somewhere and has some universal
  * values on how that occurs
  */
trait Drawable {

  /**
    * Completely remove this object
    * from wherever it is being seen
    */
  def clear(): Unit

  /**
    * Draw this object where it belongs
    * according to some other definition
    */
  def draw() : Unit

  /**
    * The behind the scenes draw that ensures
    * style has been applied, kind of a hack
    * and not performant.
    */
  def drawActual(): Unit = style{draw()}

  /**
    * Basically an alias for not having to clear
    * every call.
    */
  def redraw() : Unit = { clear() ; drawActual() }

  // UNTESTED
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
