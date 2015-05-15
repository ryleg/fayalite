package org.fayalite.ui.app.manager

import org.fayalite.ui.app.canvas
import org.fayalite.ui.app.canvas.{PositionHelpers, Canvas}
import org.fayalite.ui.app.canvas.elem.Grid
import PositionHelpers.LatCoord
import org.fayalite.ui.app.state.Input
import rx.core.Var
import rx.ops._

import scala.util.Try

/**
 * Handles all assignments to a particular lattice (hopefully
 * eventually independent of that lattice's specification
 * Handles all moving of symbols and their interactions with one another
 * @param grid: Measurement system for laying out objects and graphics
 */
class SymbolManager(grid: Grid) {
  implicit val grid_ = grid


  val symbols = Var(Map[Var[LatCoord], canvas.elem.Symbol]())
  Canvas.onKeyDown.foreach{
    q => Try{ if (q.keyCode == 8) q.preventDefault() }
  }

  Input.Key.pressCode.foreach{
    pressc =>
      val ks = pressc.toChar.toString
      val k = if (Input.Key.shift()) ks.toUpperCase else ks.toLowerCase
      if (k.length > 0) {
        val cxy = grid.cursor.latCoord
        symbols().collect {
          case (latc, sym) if latc().x >= cxy().x && cxy().y == latc().y =>
            sym.shiftRight
        }
        println("new symbol " + ks)
        val s = new canvas.elem.Symbol(Var(k.head), Var(cxy()))
        grid.cursor.shiftRight
        symbols() = symbols() ++ Map(s.latCoord -> s)
        val gclc = grid.cursor.latCoord
        if (grid.cursor.atRightEdge) {
          (1 to 1).foreach { _ => grid.cursor.shiftLeft}
          symbols().foreach { q => (1 to 1).foreach { _ => q._2.shiftLeft}}
          // shiftAllSymbolsLeft N steps // shift cursor left by a few.
        }
      }
  }

  Input.Key.downKeyCode.foreach {
    q =>
      Try {
  //      println("dangerous down" + q)
        if (q == 8) {
            val glc = grid.cursor.latCoord
            val gl = glc()
          if (gl.x > 0) {
            val lftCrs = gl.left
            symbols() = symbols().filter { q =>
              val toBeDeleted = q._1() == lftCrs
         //     println("symbol filter: " + q._2.char() + " " + toBeDeleted + " ")
              if (toBeDeleted) {
          //      println("to be delted : " + q._2.char())
                q._2.visible() = false
                val ql = q._2.latCoord
                ql() = ql().copy(x=(-5), y=(-10))
              }
              !toBeDeleted
            }
        //    println("x gt 0")
            glc() = glc().left
          }
          ()
       //   println("backspace")

        } else if (!(37 to 40).contains(q)) Try{

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
//        println("dangerous do2wn" + q)
      }

  }

}
