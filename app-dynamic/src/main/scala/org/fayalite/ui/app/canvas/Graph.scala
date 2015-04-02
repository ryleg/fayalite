package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.canvas.Schema._
import scala.scalajs.js._

import scala.scalajs.js

import scala.util.{Failure, Try}

object Graph {


  def fromJSON(g: Dynamic) = {
   // println("graphjson")
    implicit def d2l(d: Dynamic): Long = d.toString.toLong
    implicit def d2l2(d: Dynamic): String = d.toString
    //g.vertices.
    val ver = g.vertices.asInstanceOf[js.Array[Dynamic]]
    val edg = g.edges.asInstanceOf[js.Array[Dynamic]]
    val ep = edg.map {
      v: Dynamic => Edg(v.id, v.dstId, v.ed)
    }
    val vp = ver.map {
      v: Dynamic => Vtx(v.id, v.vd)
    }
    println(vp.toList)
    println(ep.toList)
    GraphData(vp, ep)
  }
}