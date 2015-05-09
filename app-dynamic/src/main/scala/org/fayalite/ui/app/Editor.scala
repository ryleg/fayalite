
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem._
import org.fayalite.ui.app.canvas.{Canvas, Schema, Graph}
import org.fayalite.ui.app.canvas.Schema.{Position, GraphData, ParseResponse}
import org.scalajs.dom.raw.MouseEvent
import rx._
import scala.scalajs.js
import scala.scalajs.js._

import scala.util.{Failure, Success, Try}

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
    import rx.ops._

    scala.util.Try {
      val lc = Var(LatCoord(0,0))
  //    val lci = lc.map{x => x}
      val s = new elem.Symbol(Var('c'), lc)
      s.drawActual()
      //Canvas.ctx.fillStyle = "red"
      //s.pos().clearAll()
    //  lc() = LatCoord(10,10)
   //   lci.reset(LatCoord(2,2))
  //    lci.parents.map{q => q.asInstanceOf[Var[LatCoord]]() = LatCoord(1,1)}
      //s.drawActual()
      //s.pos().clearAll()
     // println(s.pos().x())
    //  println(s.pos().x())
      //s.move(xyi(15,15))


    } match {
      case Success(x) => println("made hover")
      case Failure(e) => e.printStackTrace(); println("hover failed")
    }

  //val textagon = new Textagon()


  Obs(Canvas.pasteEvent, skipInitial = true) {
    println("cavnas paste event" + Canvas.pasteEvent())
  }

}

