package org.fayalite.ui.app.manager

import org.fayalite.ui.app.canvas.PositionHelpers
import org.fayalite.ui.app.canvas.PositionHelpers.{LatCoord, LatCoord2}
import org.fayalite.ui.app.canvas.elem.{Symbol1, FillStyle, Text, Grid}
import org.fayalite.ui.app.state.{Input, StateSync}

import rx.ops._

object FileGraph {

  def fromFlat(flatFiles: Array[String]) = {

  }

}

import rx._
import ops._
import org.fayalite.ui.app.canvas.elem

// trait Movable.

class D1Symbols(
                 val loc: Var[LatCoord] = Var(LatCoord(1,1)),
                 val chars: Var[Array[Char]] = Var(Array('x', 'y', 'z'))
                 )(implicit grid: Grid) {

  //println("d1symbols")
  val symbols = Rx {
    val l = loc()
    chars().zipWithIndex.map{
      case (ca, ci) =>
    //    println("making char  " + ca + " " + l.right(ci))
        val s = new elem.Symbol1(
          Var(ca),
          Var(l.right(ci)),
          overLine=Var(new FillStyle(Text.textColor))
        )(grid)
  //      grid.register(s)
        s
    }
  }

  def move(
          latCoord: LatCoord
            ) = {
    loc() = latCoord
  }

  val dragByMouse = Var(false)

  var msdn : Int = 0

  var tobs : Obs = null

  def track() =
    tobs = grid.cellXY.foreach{q =>
      move(q.copy(x=q.x-msdn))
    }
        /*
        symbols().zipWithIndex.foreach{
          case (s,i) =>
            val diff = msdn - i
            val slc = s.latticeCoordinates().copy(
             x=q.x-diff, y=q.y
            )
            s.latticeCoordinates() = slc
        }
    }*/

  implicit class vxr[T](va: Var[T]){
    def n = Var(va())
  }
  implicit def vxx[T](v: T) : Var[T] = { Var(v)}
  implicit class vvvv(vl: Var[LatCoord]) {
    def down = {
      vl() = vl().down
      vl
    }
  }

  def sym() = new Symbol1(
    'a',
    loc.n.down
  )

  dragByMouse.foreach{
    q =>
      if (q) {
        track()
      }
  }

  Obs(Input.Mouse.down, skipInitial = true) {
    if (symbols().exists{_.latticeCoordinates() == grid.cellXYDown()}) {
      msdn = symbols().zipWithIndex.collectFirst{
        case (q,i) if q.latticeCoordinates() ==
        grid.cellXYDown() => i}.get
      sym()
      dragByMouse() = true
      }
    }

  Obs(Input.Mouse.up, skipInitial = true) {
      dragByMouse() = false
      tobs.kill()
  }

  symbols.reduce{
     (a: Array[elem.Symbol1], b: Array[elem.Symbol1]) =>
      a.foreach{q => q.off; } //grid.deregister(q)}
       b.foreach{_.redraw()}
      b
  }


} // editor.splat(area) add area onto editor in free location.

import PositionHelpers._

class FileGraph(editor: Editor) {
  implicit val grid = editor.grid

  val maxLen = 4

 // val numBlocks = grid.gridTranslator.numColumns.map{_ / maxLen}
 // new D1Symbols()
/*  Array.tabulate(1){i =>
    val lci = LatCoord(i*maxLen ,0)
    new D1Symbols(loc=Var(lci))
  }*/
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
  /*

  Ex:
 latcoord dx dy map (a_i * a_j ex : wealth distr map ** elevation weighted
 by linear func according to domain of draggable ui event.
  DXDY : Optimal path * domain * range * weight.
   */

}
