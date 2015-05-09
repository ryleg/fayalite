package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
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
                val flashing: Boolean = false
                )
              (implicit val grid: Grid, val cs: CanvasStyling =
              CanvasStyling(fillStyle= "#FFC66D", globalAlpha = .7))
  extends GridElement(xyi, offset=offset)(grid)
  //with Flasher // TODO: Fix
 {
  //xyi.foreach{i => println(i.str)}
  //flash() = flashing
  //override val area = dxDy

  def draw() : Unit = pos().fillRect()

}
