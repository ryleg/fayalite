package org.fayalite.layer

import ammonite.ops._
import ammonite.ops.ls.rec
import org.fayalite.Fayalite
import Fayalite._
import scala.util.Try
import scalaz.Scalaz._
import rx._

/**
 * Returns raw code files primarily for serving in the UI
 * Actually like legitimately useful although painfully incomplete.
 */
object FSMan {

  def main(args: Array[String]) {
    fileGraph().foreach{println}
  }
  val ignore = List("target", ".idea", "lib", ".git", ".DS_Store", ".dll", ".js")

  def fileGraph(
                 startPath: Path = cwd./(RelPath("app-dynamic")) /
                   'src / 'main / 'scala / 'org / 'fayalite / 'ui / 'app) = {

        val r = rec.!!(startPath).toList
          r.toArray.map{_.toString()}
    /*
    def innerExplore(lsq: LsSeq, parentIds: Seq[Long] = Seq()) : Unit = {
      lsq.map{ p =>
        val seg = p.-(cwd).segments
        if (!ignore.exists(i => seg.last.contains(i))) {
          val id = gb.addV(p.last.toString, parentIds)
          println(seg)
          if (p.isDir) {
            innerExplore(ls ! p, Seq(id))
          } else {
            Try {
              val contents = read.lines.!!(p)
              gb.addV(contents.toSeq.mkString("\n").slice(0, 200), Seq(id))
            }
          }
        }
      }
    }
  }*/
  }
}

class FSMan {


}