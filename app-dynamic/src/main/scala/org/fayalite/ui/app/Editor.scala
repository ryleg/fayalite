
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.ElementFactory.Text
import org.fayalite.ui.app.canvas.{Canvas, ElementFactory, Schema, Graph}
import org.fayalite.ui.app.canvas.Schema.{Position, GraphData, ParseResponse}
import org.scalajs.dom.raw.MouseEvent
import rx._
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

  val xButtonBuffer = 10
  val yButtonBuffer = 10

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

  val editOffsetY = Var(400)
}

class Node(
            val text: Var[ElementFactory.Text],
            val editable: Var[Option[ElementFactory.Text]] = Var(None)
        //    val subText: Var[ElementFactory.Text]
            ) {
  import Editor._

  val resize = Obs(Canvas.onresize, skipInitial = true) {
    println("OnResize")
    text().redraw()
  }
  
  def isInside = checkInside(text().position(), Canvas.onclick())

  def drawEditable() = {
    editable().map{e =>
      e.redraw()
    }
  }

  val click = Obs(Canvas.onclick) {
    if (isInside) {
      println("click on " + text().text())
     // drawEditable()
    }
    }
}

//class EditNode(val text: Var[ElementFactory.Text])

class Editor() {

  import Editor._

  val graph = Var(GraphData(Array(), Array()))

  val layout = Rx {
    val g = graph()
    val emap = g.edges.map{ e => e.id -> e}.toMap
  //  val emap2 = g.edges.map{ e => e.dstId -> e}.toMap
    val emap3 = g.edges.map{ e => e.id -> e}.groupBy{_._1}.toMap

    val i2v = g.vertices.map{v => v.id -> v}.toMap


    val vesl = g.vertices.map{v => emap.get(v.id).map{_ -> v}}.flatten.toMap

  //  val ves = g.vertices.map{v => (v, emap.get(v.id))}



    val curY = Var(bodyOffsetY())

    val classF = g.vertices.filter{q => q.vd.charAt(0).isUpper && q.vd.length < 100}

    classF.grouped(numColumns()).toList.foreach {
      gro =>
        val curX = Var(bodyOffset())
        curY() += 37
        println("curY " + curY())
        Schema.TryPrintOpt {
          gro.zipWithIndex.foreach {
            case (vtx, idx) =>
              println("draw node " + vtx + idx)
              val childs = emap3.get(vtx.id).map {
                c =>
                  println("child nodes " + c.map {
                    _._2.dstId
                  }.toList)
                  c.map {
                    _._2.dstId
                  }.map {
                    i2v.get
                  }.head
              }.flatten.map {
                cv =>
                  new Text(Var(cv.vd), x = Var(curX()), y = Var(editOffsetY())) // x y not used
              }
              //   + idx*maxNodeWidth().toInt

              val tex = new Text(Var(vtx.vd), x = Var(curX()), y = Var(curY()))
              val n = new Node(Var(tex), editable = Var(childs))

              n.text().redraw()
              //  n.drawText()()
              curX() += n.text().position().dx.toInt + 30
              /*   ElementFactory.getDrawText(vtx.vd, font=s"12pt Calibri")(
          bodyOffset() + idx*maxNodeWidth().toInt,
          bodyOffsetY())._1()*/
              //
              //    n.drawText()
              n
          }
        }

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
