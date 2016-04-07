package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.{PositionHelpers, Canvas}
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling

import rx._
import rx.ops._
import PositionHelpers._

object GridRect {

}


import rx._
import rx.ops.{Timer, DomScheduler}

import scala.util.Try
import scala.concurrent.duration._


class GridRect(
                xyi: VL = l0,
                offset: VLD = ld0,
                val dxDy: VLD = ld0,
                val flashing: Boolean = true,
                val fill: String = "#FFC66D",
              val alpha: Double = .6D,
              val extraFill: List[LatCoordD] = List()
                )
              (implicit grid: Grid)
  extends GridElement(xyi, offset=offset, area=Some(dxDy)
  )(grid)
  with Flasher
 {
 // override val styling = CanvasStyling(fillStyle= "#FFC66D", globalAlpha = .7)
  flash() = flashing
  override def clear(): Unit = {
    val p = pos()
    extraFill.map{f => p.copy(xy2=f).clearAll()}
    pos().clearAll()
  }
  def draw() : Unit = {
    Canvas.ctx.fillStyle = fill
    Canvas.ctx.globalAlpha = alpha
    val p = pos()
    extraFill.map{f => p.copy(xy2=f).fillRect()}
    pos().fillRect()

  }

}
