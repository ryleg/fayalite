package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema._
import org.fayalite.ui.app.canvas.elem.Text._
import org.scalajs.dom.raw.MouseEvent

import rx._
import rx.ops._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

class Cursor(
              val position: Var[Position],
              val underlyingRedraws: Var[Act] = Var(Act0)
              ) {

  import Cursor._
  implicit val doms = new DomScheduler()

  val blinkRate = Var(400) // in millis

  val t = Timer(600 millis)

  def clear() = {
    val p = position()
    p.clear()
    underlyingRedraws()()
  }

  val active = Var(false)

  val draw = Rx {() => {
    val p = position()
    clear()
    val xj = p.x
    val yj = p.y
    Canvas.ctx.fillStyle = "red"
    Canvas.ctx.globalAlpha = .8
    Canvas.ctx.fillRect(xj, yj, p.dx, p.dy)
  }}

  val o = Obs(t) {
    val a = active()
    active() = !a
    if (a) draw()() else clear()
  }
}


object Cursor {

  def apply(x: Int, y: Int) = new Cursor(
    Var(Position(x-5, y, -5, 20))
  )

  def textToCursorCoordinates(t: Text,
                                  me: MouseEvent) = {
    val cp = t.splitText().map{st => st.canvasIdx}
    val ils = interLineSpacing()
    val y0 = t.y()
    val yIdx = Math.abs((me.clientY-y0)/ils).toInt
  //  println("yIdx " + yIdx)
    val lineElem = cp(yIdx)
    val dxi = cp(yIdx).collectFirst{
      case cti : CharTextIdx if cti.width + t.x() > me.clientX
      => cti.width
    }.getOrElse(t.widths().max)
    val yj = t.y() + (yIdx-1)*ils
    val xj = t.x() + dxi
    (xj, yj)
  }

}