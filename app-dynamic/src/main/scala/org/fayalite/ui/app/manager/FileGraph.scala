package org.fayalite.ui.app.manager

import org.fayalite.ui.app.canvas.PositionHelpers
import org.fayalite.ui.app.canvas.PositionHelpers.{LatCoord, LatCoord2}
import org.fayalite.ui.app.canvas.elem.{FillStyle, Text, Grid}
import org.fayalite.ui.app.state.{Input, StateSync}

import rx.ops._

object FileGraph {

  def fromFlat(flatFiles: Array[String]) = {

  }

}

import rx._
import ops._
import org.fayalite.ui.app.canvas.elem

class D1Symbols(
                 val loc: Var[LatCoord] = Var(LatCoord(1,1)),
                 val chars: Var[Array[Char]] = Var(Array('x', 'y', 'z'))
                 )(implicit grid: Grid) {

  println("d1symbols")
  val symbols = Rx {
    val l = loc()
    chars().zipWithIndex.map{
      case (ca, ci) =>
        println("making char  " + ca + " " + l.right(ci))
        val s = new elem.Symbol(
          Var(ca),
          Var(l.right(ci)),
          overLine=Var(new FillStyle(Text.textColor))
        )(grid)

  //      grid.register(s)
        s
    }
  }

  val dragByMouse = Var(false)

  Obs(Input.Mouse.down, skipInitial = true) {
    if (symbols().exists{_.latticeCoordinates() == grid.cellXYDown()}) {
      dragByMouse() = true
    }
  }

  Obs(Input.Mouse.up, skipInitial = true) {
      dragByMouse() = false
  }


  symbols.reduce{
     (a: Array[elem.Symbol], b: Array[elem.Symbol]) =>
      a.foreach{q => q.off; }//grid.deregister(q)}
      b
  }


} // editor.splat(area) add area onto editor in free location.

import PositionHelpers._

class FileGraph(editor: Editor) {
  implicit val grid = editor.grid

  val maxLen = 4

 // val numBlocks = grid.gridTranslator.numColumns.map{_ / maxLen}
 // new D1Symbols()
  Array.tabulate(5){i =>
    val lci = LatCoord(i*maxLen ,0)
    new D1Symbols(loc=Var(lci))
  }
/*
  StateSync.meta.foreach{
    q =>
      if (q != null) {
        val sr = q.classRefs.map {
          c =>
            c.split("/")
        }
        val tl = sr.minBy {
          _.size
        }
        val ca = tl.last.replaceAll(".scala", "").toCharArray
        new D1Symbols(vl(1, 3), ca.v)
      }
  }
  */

}
