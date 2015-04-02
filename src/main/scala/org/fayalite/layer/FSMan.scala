package org.fayalite.layer

import ammonite.ops._
import org.fayalite.Fayalite
import Fayalite._
import scala.util.Try
import scalaz.Scalaz._
import rx._

object FSMan {
  def main(args: Array[String]) {
    fileGraph().p
  }
  case class Vertex(id: Long, vd: String)

  case class Edge(id: Long, dstId: Long, ed: String)

  case class Graph(vertices: List[Vertex], edges: List[Edge])

  class GraphBuilder() {

    val vertices = Var(List[Vertex]())
    val edges = Var(List[Edge]())

    val currentId = Var(1L)

    def toGraphJson = {
      Graph(vertices(), edges()).json
    }

    def toGraph = {
      Graph(vertices(), edges())
    }

    def addV(vd: String, parentEdges: Seq[Long]) = {
      val id = currentId()
      vertices() :+= Vertex(id, vd)
      edges() = edges() ++ parentEdges.map{peid => Edge(peid, id, "")}
      currentId() += 1L
      id
    }
  }


  val ignore = List("target", ".idea", "lib", ".git", ".DS_Store", ".dll", ".js")

  def fileGraph(
                 startPath: Path = cwd./(RelPath("app-dynamic")) /
                   'src / 'main / 'scala / 'org / 'fayalite / 'ui / 'app ,
                  procFile: Path => Unit = (pf: Path) => ()
                 ) = {
    val processingStart = ls ! startPath
    val gb = new GraphBuilder()
    def innerExplore(lsq: LsSeq, parentIds: Seq[Long] = Seq()) : Unit = {
      lsq.map{ p =>
        val seg = p.-(cwd).segments
        if (!ignore.exists(i => seg.last.contains(i))) {
          val id = gb.addV(p.last.toString(), parentIds)
          println(seg)
          if (p.isDir) {
            innerExplore(ls ! p, Seq(id))
          } else {
            /*Try {
              val contents = read.lines.!!(p)
              gb.addV(contents.toSeq.mkString, Seq(id))
            }*/
          }
        }
      }
    }
    innerExplore(processingStart)
    gb.toGraph
  }
}

class FSMan {


}