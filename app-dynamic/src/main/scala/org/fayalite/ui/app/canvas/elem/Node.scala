package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema.Position
import org.scalajs.dom.raw.MouseEvent

import rx._
import rx.ops.{Scheduler, Debounce}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.Date
import scala.util.Try

object Node{
/*
  class Debouce[T](f: => T){
    val lastClickTime = Var(time)
      def call()  : Option[T] = {
        if (time > lastClickTime() + 300) {
          Some(f)
        } else None
      }
  }*/

  def time = new Date().getTime()
  val xButtonBuffer = 10
  val yButtonBuffer = 10

  import scala.concurrent.duration._

  @deprecated
  implicit def posToPosition(pos: Pos) : Position = {
    Position(pos.x(), pos.y().toInt, pos.dx(), pos.dy().toInt)
  }

  def checkInside(position: Position, me: MouseEvent) = {
    Try {
      val sxi = me.screenX
      val syi = me.screenY
      val cxi = me.clientX
      val cyi = me.clientY
      (cxi > position.x - xButtonBuffer) &&
        (cxi < position.x2 + xButtonBuffer) &&
        (cyi > position.y - yButtonBuffer) &&
        (cyi < position.y2 + yButtonBuffer)
    }.toOption.getOrElse(false)
  }


}

class Node(
        //    val text: Var[Text],
     //       val editable: Var[Option[Text]] = Var(None)
            ) {

  import Node._
  import Text._
/*
  val resize = Obs(Canvas.onresize, skipInitial = true) {
    //  println("OnResize")
    text().redraw()
  }

  def isInside = checkInside(text().position(), Canvas.onclick())

  def drawEditable() = {
    editable().map { e =>
      e.redraw()
    }
  }

  /*
  val clickDebounce = new Debounce{
    println(time)
    println(lastClickTime())
    println("click on " + text().text())
    drawEditable()
    lastClickTime() = time
  }*/

  import scala.concurrent.duration._


  var lastClickTime = time
  val click = Obs(Canvas.onclick) {
    //val textAtTime = text()
    val me = Canvas.onclick()
    println("Bad Obs canvas" +
      s"${me.clientX} ${me.clientY}"
    )
/*
   editable().foreach{t => if
   (checkInside(t.position(), me)){

     Cursor(t, me)

      }


   //   text().setEditable()

    }

      if (checkInside(text().position(), me)) {

        Cursor.cursor.show() = false
      //  if (isInside) {
   //   println(" Bad isinside")
      drawEditable()

    }*/
      /*     if (time > lastClickTime + 300) {
             println( " Bad click on ")
             println(time)
             println(lastClickTime)
             println("click on " + text().text())
             lastClickTime = time
             //  }
           }
         }*/*/

}
