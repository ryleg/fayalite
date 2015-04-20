package org.fayalite.ui.app.canvas

import org.scalajs.dom._
import org.scalajs.dom
import scalajs.js._

import rx._
import rx.ops._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Input {

}


/**
 * Need to figure out a workaround to access clipboard data directly.
 * Wasn't available in previous scala.js versions, maybe now or soon?
 */
object Input {

  object Mouse {
    val move = Var(null.asInstanceOf[MouseEvent])
    dom.window.onmousemove = (me: MouseEvent) =>
      move() = me
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