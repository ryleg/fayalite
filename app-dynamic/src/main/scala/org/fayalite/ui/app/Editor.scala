
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem._
import org.fayalite.ui.app.canvas.{Input, Canvas, Schema, Graph}
import org.fayalite.ui.app.canvas.Schema.{Position, GraphData, ParseResponse}
import org.scalajs.dom.raw.MouseEvent
import rx._
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
  //val cxy = grid.cursorXY.map { q => q.offset}
  //val cxyActual = cxy.map { q => q: LatCoordD}
  val symbols = Var(Map[Var[LatCoord], Symbol]())
  Canvas.onKeyDown.foreach{
    q => Try{ if (q.keyCode == 8) q.preventDefault() }
  }
 // Input.Key.downKeyCode.foreach {
  //  kc =>
    //  if (kc != 8) {

  Input.Key.downKeyCode.foreach {
    q =>
      Try {
        println("dangerous down" + q)
        if (q == 8) {
            val glc = grid.cursor.latCoord
            val gl = glc()
          if (gl.x > 0) {
            val lftCrs = gl.left
            symbols() = symbols().filter { q =>
              val toBeDeleted = q._1() == lftCrs
              println("symbol filter: " + q._2.char() + " " + toBeDeleted + " ")
              if (toBeDeleted) {
                println("to be delted : " + q._2.char())
                q._2.visible() = false
                val ql = q._2.latCoord
                ql() = ql().copy(x=(-5), y=(-10))
              }
              !toBeDeleted
            }
            println("x gt 0")
            glc() = glc().left
          }
          ()
          println("backspace")

        } else if (!(37 to 40).contains(q)) Try{
          val ks = q.toChar.toString
          val k = if (Input.Key.shift()) ks.toUpperCase else ks.toLowerCase
          if (k.length > 0) {
            val cxy = grid.cursor.latCoord
            symbols().collect {
              case (latc, sym) if latc().x >= cxy().x && cxy().y == latc().y =>
                sym.shiftRight
            }
            println("new symbol " + ks)
            val s = new elem.Symbol(Var(k.head), Var(cxy()))
            grid.cursor.shiftRight
            symbols() = symbols() ++ Map(s.latCoord -> s)
            val gclc = grid.cursor.latCoord
            if (grid.cursor.atRightEdge) {
              (1 to 1).foreach { _ => grid.cursor.shiftLeft}
              symbols().foreach { q => (1 to 1).foreach { _ => q._2.shiftLeft}}
              // shiftAllSymbolsLeft N steps // shift cursor left by a few.
            }
          }
        } else {
          val crl = grid.cursor
          val cll = crl.latCoord
          val clq = cll()
          q match {
            case 37 => crl.shiftLeft
            case 38 => cll() = clq.up
            case 39 => cll() = clq.right
            case 40 => cll() = clq.down
            case _ =>
          }
        }
        println("dangerous do2wn" + q)
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
  //    lci.parents.map{q => q.asInstanceOf[Var[LatCoord]]() = LatCoord(1,1)}  lc  aaaaaaaaaaaaaa             i.parents.map{q => q.asInstanceOf[Var[LatCoord]]() = LatCoord(1,1)}
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

