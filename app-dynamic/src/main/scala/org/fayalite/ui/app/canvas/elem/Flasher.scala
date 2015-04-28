package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema._
import rx._
import rx.ops.{Timer, DomScheduler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import scala.concurrent.duration._

trait Flasher extends Drawable {

  val active = Var(false)
  val flash = Var(false)
  implicit val doms = new DomScheduler()
  val blinkRate = 650 // in millis

  val t = Timer(blinkRate.milliseconds)
  val o = Obs(t) {
    if (flash()) {
      val a = active()
      active() = !a
      if (a) draw()
      else {
        clear()
      }
    }
  }
}


