package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.canvas.Schema._
import org.fayalite.ui.app.canvas.elem.PositionHelpers
import org.scalajs.dom._
import org.scalajs.dom
import scala.util.Try
import scalajs.js._

import rx._
import rx.ops._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Input {

}


import rx.ops._

/**
 * Need to figure out a workaround to access clipboard data directly.
 * Wasn't available in previous scala.js versions, maybe now or soon?
 */
object Input {

  def time = {
    import scala.scalajs.js.Date
    new Date().getTime()
  }

  // TODO : Move old canvas input references here.

  // Need to kill clipboard Obs on dynamic class reload.

  object Mouse extends PositionHelpers {

    implicit def mouseEventXY(me: MouseEvent) : XY =
      Try{adjustOffset(xy(me.clientX, me.clientY))}.toOption.getOrElse(xy(0D, 0D))

    def adjustOffset(xyi: XY) = {
      val bb = dom.document.body.getBoundingClientRect()
      xyi.x() -= bb.left
      xyi.y() -= bb.top
      xyi
    }

    val moveRaw = Var(null.asInstanceOf[MouseEvent])
    val move = Var((0D, 0D))
    val click = Var(xy())

    dom.window.onmousemove = (me: MouseEvent) => {
      moveRaw() = me
      move() = {
        /*val bb = dom.document.body.getBoundingClientRect()
        (me.clientX - bb.left, me.clientY - bb.top)*/
        val ao : XY = me
        ao.x() -> ao.y()
      }
    }

/*    Obs(downKeyCode, skipInitial = true) {
      println("downKeyCode OBS " + downKeyCode())
    }*/

    val to = Canvas.onclick.foreach{ me => click() = me : XY }

  }

  object Key extends PositionHelpers {

    val keyDown = Canvas.onKeyDown

    val downKeyCode = Var(-1)

    /**
     * left arrow	37
up arrow	38
right arrow	39
down arrow	40
     */
    object Arrow {
      val left = Var(time)
      val right = Var(time)
      val up = Var(time)
      val down = Var(time)
      val onDown = Var(0)
      def apply(keyCode: Int) : Unit = {
        onDown() = keyCode
        keyCode match {
          case 37 => left() = time
          case 38 => up() = time
          case 39 => right() = time
          case 40 => down() = time
          case _ =>
        }
      }
      downKeyCode.foreach{d => Arrow(d)}
    }

    val keyDownCode = keyDown.map{q =>
      downKeyCode() = q.keyCode
      q.keyCode
    }

  }



  implicit val scheduler = new DomScheduler()

  val clipboard = Var("")

  val t = Timer(100.milliseconds)

  Obs(t) {
    //println("timer")
    val cc = clipboard()
    val nc = getClipboard
    if (nc != cc) {
   //   println("reassigning clipboard " + nc + " newold " + cc)
      clipboard() = nc
    }
  }

  /**
   * Get an HTML element's text that auto-updates with paste contents.
   * @return Pasted string
   */
  def getClipboard = {
    val clip = dom.document.body
      .getElementsByTagName("div")(1).textContent
 //   println("clipboard: " + clip)
    clip
  }

}