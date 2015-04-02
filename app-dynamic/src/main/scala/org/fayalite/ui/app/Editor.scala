
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.{Canvas, ElementFactory, Schema, Graph}
import org.fayalite.ui.app.canvas.Schema.{Position, GraphData, ParseResponse}
import rx._
import scala.scalajs.js._

import scala.util.Try

object Editor{


  val editor = Var(null.asInstanceOf[Editor])

  def apply() = {
    editor() = new Editor()
  }

  val bodyOffset = Var(122)

  val bodyOffsetY = Var(122)


  val maxNodeWidth = Var(100D)

  val numColumns = Rx{((Canvas.width - bodyOffset()) / maxNodeWidth()).toInt}

}

class Node(
            val text: Var[String],
            val x: Var[Int],
            val y: Var[Int]
            ) {
  import Editor._

  val drawText = Rx{
    ElementFactory.getDrawText(text(),
      //Some(maxNodeWidth())
       font=s"12pt Calibri"
    )(x(), y())._1
  }

  val position = Rx {
    ElementFactory.getDrawText(text()//,
     // Some(maxNodeWidth())
    )(x(), y())._2
  }

  val resize = Obs(Canvas.onresize, skipInitial = true) {
    position().clear()
    drawText()()
  }


}

class Editor() {

  import Editor._

  val graph = Var(GraphData(Array(), Array()))

  val layout = Rx {
    val g = graph()
    val emap = g.edges.map{ e => e.id -> e}.toMap
    val ves = g.vertices.map{v => (v, emap.get(v.id))}
    val curX = Var(bodyOffset())
    ves.filter{_._2.isEmpty}.slice(0, numColumns()).map{_._1}.zipWithIndex.map{
      case (vtx, idx) =>
        println("draw node " + vtx + idx)
       //   + idx*maxNodeWidth().toInt
        val n = new Node(Var(vtx.vd), Var(curX()), bodyOffsetY)
        n.drawText()()
        curX() += n.position().dx.toInt
     /*   ElementFactory.getDrawText(vtx.vd, font=s"12pt Calibri")(
          bodyOffset() + idx*maxNodeWidth().toInt,
          bodyOffsetY())._1()*/
    //
    //    n.drawText()
        n
    }

  }

  PersistentWebSocket.sendKV("tab", "Editor",
    (pr: Dynamic) => {
      Schema.TryPrintOpt{
        graph() = Graph.fromJSON(pr.graph)
      }
    }
  )



}
