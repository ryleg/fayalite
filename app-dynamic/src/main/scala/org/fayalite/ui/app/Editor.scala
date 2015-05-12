
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem._
import org.fayalite.ui.app.canvas.{Input, Canvas, Schema, Graph}
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
import rx.ops._

/**
 * Handles all assignments to a particular lattice (hopefully
 * eventually independent of that lattice's specification
 * Handles all moving of symbols and their interactions with one another
 * @param grid: Measurement system for laying out objects and graphics
 */
class SymbolManager(grid: Grid) {
  implicit val grid_ = grid
  import grid.gridTranslator._
  val cxy = grid.cursorXY.map{q => q.offset}
  val cxyActual = cxy.map{q => q: LatCoordD}
  val symbols = Var(Map[LatCoord, Symbol]())
  Input.Key.downKey.foreach {
    k =>
      val toRight = grid.cols - cxy().x
      symbols().collect {
        case (latc, sym) if latc.x >= cxy().x => //&& cxy().y == latc.y =>
          //val movedSymbolDown = sym.shiftRight

      }
      val s = new elem.Symbol(Var(k.head), Var(cxy()))
      symbols() = symbols() ++ Map(s.latCoord() -> s)

      val glc = grid.cursor.latCoord
      val lc = glc()
      glc() = {
        if (lc.right.x >= grid.cols - 1) {
          lc.left0.down
        } else lc.right
      }
  }

}
class Editor() {


  //plusZoom.redraw()
  val grid = Grid()

  val symbolManager = new SymbolManager(grid)

  implicit val grid_ = grid
    import rx.ops._

    scala.util.Try {
      val lc = Var(LatCoord(0,0))
  //    val lci = lc.map{x => x}

     // s.drawActual()
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
  Obs(Canvas.pasteEvent, skipInitial = true) {
    println("cavnas paste event" + Canvas.pasteEvent())
  }

}

