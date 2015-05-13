package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema._
import org.fayalite.ui.app.state.Input
import rx._
import rx.ops.{Timer, DomScheduler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import scala.concurrent.duration._

object Flasher {
}

trait Flasher extends Drawable {
  import Flasher._
  import rx.ops._

  val flash = Var(true)
  val o = Obs(Input.flashRate) {
    if (flash()) {
      val prev = !visible()
      visible() = prev
    }
  }
  flash.foreach{
    f =>
      if (!f) visible() = true
  }

}
