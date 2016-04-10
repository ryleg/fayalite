/*
package org.fayalite.sjs.canvas

import org.fayalite.sjs.Schema.CanvasStyling
import org.scalajs.dom.raw.CanvasRenderingContext2D

/**
  * Created by aa on 3/17/2016.
  */
trait CanvasHelp {

  val defaultStyling = CanvasStyling()

  /**
    * Canvas context requires setting flags before any operation, hence this wrapper.
 *
    * @param f: Render func
    * @tparam T: Render return type
    * @return : Result of render func
    */
  def style[T](f : => T)(implicit ctx: CanvasRenderingContext2D,
  stylingEv: CanvasStyling = CanvasStyling())
  //           )
  : T = {
    val prevFont = ctx.font
    val prevFillStyle = ctx.fillStyle
    val prevAlpha = ctx.globalAlpha
    ctx.font = stylingEv.font
    ctx.fillStyle = stylingEv.fillStyle
    ctx.globalAlpha = stylingEv.globalAlpha
    val ret = f
    ctx.font = prevFont
    ctx.fillStyle = prevFillStyle
    ctx.globalAlpha = prevAlpha
    ret
  }
}
*/
