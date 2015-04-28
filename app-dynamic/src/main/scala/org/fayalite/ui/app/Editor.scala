
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem._
import org.fayalite.ui.app.canvas.{Canvas, Schema, Graph}
import org.fayalite.ui.app.canvas.Schema.{Position, GraphData, ParseResponse}
import org.scalajs.dom.raw.MouseEvent
import rx._
import scala.scalajs.js
import scala.scalajs.js._

import scala.util.Try

object Editor {

  val editor = Var(null.asInstanceOf[Editor])

  def apply() = {
    editor() = new Editor()
  }

  val bodyOffset = Var(122)
  val bodyOffsetY = Var(122)
  val maxNodeWidth = Var(100D)
  val rightOffset = Var(300)
  val numColumns = Rx {
    ((Canvas.width - bodyOffset() - rightOffset()) / maxNodeWidth()).toInt
  }
  val editOffsetY = Var(400)
}



//class EditNode(val text: Var[ElementFactory.Text])
import org.fayalite.ui.app.canvas.elem
import PositionHelpers._

class Editor() {


  //plusZoom.redraw()
  val grid = Grid()

  implicit val grid_ = grid


  val s = new elem.Symbol(Var('c'), XYI(Var(5), Var(5)))

  s.draw()
  //val textagon = new Textagon()


  Obs(Canvas.pasteEvent, skipInitial = true) {
    println("cavnas paste event" + Canvas.pasteEvent())
  }

}

