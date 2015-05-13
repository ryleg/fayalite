package org.fayalite.ui.app.state

import org.fayalite.ui.app.canvas.{PositionHelpers, Canvas}
import PositionHelpers.LatCoordD
import org.scalajs.dom
import org.scalajs.dom._
import rx._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

class Input {

}


import rx.ops._

/**
 * Need to figure out a workaround to access clipboard data directly.
 * Wasn't available in previous scala.js versions, maybe now or soon?
 */

import PositionHelpers._

object Input {

  def time = {
    import scala.scalajs.js.Date
    new Date().getTime()
  }

  // TODO : Move old canvas input references here.

  // Need to kill clipboard Obs on dynamic class reload.



  object Mouse {



    implicit def mouseEventXY(me: MouseEvent) : LatCoordD =
      Try{adjustOffset(xy(me.clientX, me.clientY))}.toOption.getOrElse(xy(0D, 0D))

    def adjustOffset(xyd: LatCoordD) = {
      val bb = dom.document.body.getBoundingClientRect()
      xyd.-(xy(bb.left, bb.top))
    }

    val moveRaw = Var(null.asInstanceOf[MouseEvent])
    val move = Var(LatCoordD(0D, 0D))
    val click = Var(xy())
    val moveScreen = Var(LatCoordD(0D, 0D))

    val onScroll = Var(null.asInstanceOf[UIEvent])
    dom.window.onscroll = (uie: UIEvent) => {
      uie.preventDefault()
      uie.stopPropagation()
      onScroll() = uie
    }

    dom.window.onmousemove = (me: MouseEvent) => {      moveRaw() = me
      //me.preventDefault()
      //me.stopPropagation()
      val lcdq = me : LatCoordD

      move() = lcdq //LatCoordD(me.clientX, me.clientY)
   //   moveScreen() = LatCoordD(me.screenX, me.screenY)
    }

/*    Obs(downKeyCode, skipInitial = true) {
      println("downKeyCode OBS " + downKeyCode())
    }*/

    val to = Canvas.onclick.foreach{ me => click() = me : LatCoordD }

    val dragStart = Var(null.asInstanceOf[DragEvent])
    dom.window.ondragstart = (de: DragEvent) => {
      println("dragStart")
      dragStart() = de
    }
    val dragEnd = Var(null.asInstanceOf[DragEvent])
    dom.window.ondragend = (de: DragEvent) => {dragEnd() = de}

    val down = Var(null.asInstanceOf[LatCoordD])
    val up = Var(null.asInstanceOf[LatCoordD])
    dom.window.onmouseup = (me: MouseEvent) => {
      Try{up() = me}}
    dom.window.onmousedown = (me: MouseEvent) => {Try{down() = me}}



  }

  object Key {

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
      downKeyCode.foreach{d => ;Arrow(d)}
    }
    val downKey = Var("")
    val shift = Var(false)
    val keyDownCode = keyDown.map{q =>
      Try{val s = q.shiftKey; if (s) shift() = true}
      Try{downKeyCode() = q.keyCode}
      Try{println("keyc" + q.keyCode.toChar)}
      Try{println(q.keyCode)}
      Try{println(q.keyCode.toChar.toString.length + "len")}
      //Try{println(q.shiftKey)}
      //Try{println("up" +q.keyCode.toChar.toUpper)}
      //Try{println("lo" + q.keyCode.toChar.toLower)}
      Try {
        val k = q.keyCode.toChar.toLower.toString
        downKey() = {
          if (q.shiftKey) k.toUpperCase else k
        }
      }
     // println(q.key == undefined)
     // println(q.key)
      //Try{if (q.key != null) downKey() = q.key}
      Try{q.keyCode}
      shift() = false
    }
  }

  implicit val scheduler = new DomScheduler()

  val clipboard = Var("")

  val t = Timer(100.milliseconds)
  val flashRate = Timer(650.milliseconds)

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