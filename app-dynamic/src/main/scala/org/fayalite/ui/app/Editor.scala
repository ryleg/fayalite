
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem.{Node, ElementFactory, Text}
import org.fayalite.ui.app.canvas.{Canvas, Schema, Graph}
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


  val editOffsetY = Var(400)
}



//class EditNode(val text: Var[ElementFactory.Text])

class Editor() {


  val test = Text("test", 200, 200)
  test.redraw()
  val nodet = new Node(Var(test), Var(Some(Text("testline\ntestlinet\n\nasdf", 200, 300))))

  println("new editor")
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

/*    val nodes = classF.grouped(numColumns()).toList.map {
      gro =>
        val curX = Var(bodyOffset())
        curY() += 37
     //   println("curY " + curY())
        Schema.TryPrintOpt {
          gro.zipWithIndex.map {
            case (vtx, idx) =>
              val childs = emap3.get(vtx.id).map {
                c =>
                  c.map {
                    _._2.dstId
                  }.map {
                    i2v.get
                  }.head
              }.flatten.map {
                cv =>
                  new Text(Var(cv.vd), x = Var(100), y = Var(400)) // x y not used
              }
              val tex = new Text(Var(vtx.vd), x = Var(curX()), y = Var(curY()))
              val n = new Node(Var(tex), editable = Var(childs))
              n.text().redraw()
              curX() += n.text().position().dx.toInt + 30
              n
          }
        }
    }

    val flatNodes = nodes.flatten.flatten.toSet

    flatNodes.foreach{n => println(n.text().text())}*/
/*
    flatNodes.foreach{
      n =>
        n.editable().map{e => Obs(e.onRedraw, skipInitial=true){
          flatNodes.filter{_ != n}.foreach{
            ne =>
              ne.editable().map{_.position().clear()}
          }
        }}
    }*/


  }

  PersistentWebSocket.sendKV("tab", "Editor",
    (pr: Dynamic) => {
      Schema.TryPrintOpt{
        graph() = Graph.fromJSON(pr.graph)
      }
    }
  )



}
