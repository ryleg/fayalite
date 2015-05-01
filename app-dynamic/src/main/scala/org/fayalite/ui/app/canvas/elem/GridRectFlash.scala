package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling

import rx._
import rx.ops._
import PositionHelpers._

object GridRectFlash {
  
}

// TODO : Switch to rectangle class.

/**
 * Horizontal or vertical is really all that arbitrary means.
 */
class GridRectFlash(
                       xyi: XYI,
                       offset: XY,
                       dxDy: XY
                       )
                     (implicit grid: Grid, cs: CanvasStyling =
                     CanvasStyling(fillStyle= "#FFC66D", globalAlpha = .7))
  extends GridElement(xyi, offset=offset, dxDy=dxDy)(grid) with Flasher {

  def draw() : Unit = pos().fillRect()

  def clear() : Unit = pos().clearAll()

}
