package org.fayalite.refl

import ammonite.ops._

object BuildDoc {

  val tmp = home / 'tmp / 'tmpl
  mkdir(tmp)

  def xf(p: Path) = {

    import scala.sys.process._
    val t = tmp / p.last
    mkdir(t)
    val pb = Process(Seq("jar", "xf",
      {ls (p / 'docs)}
        .iterator.next().toString())
      , new java.io.File(t.toString())
    )

    pb.!!

  }



  def main(args: Array[String]) {
    val ic = home / RelPath(".ivy2") / 'cache
    val lh = ic / RelPath("com.lihaoyi")
    val sjs = ic / RelPath("org.scala-js")
    val dom = sjs / RelPath("scalajs-dom_sjs0.6_2.11")
    import fa._

    /*Seq("upickle_sjs0.6_2.11", "scalarx_sjs0.6_2.11", "scalatags_sjs0.6_2.11") m {
      lh / RelPath(_)
    } fe { xf }
    xf ( sjs / RelPath("scalajs-library_2.11"))*/

    xf(dom)

  }
}
