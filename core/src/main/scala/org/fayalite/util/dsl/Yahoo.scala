package org.fayalite.util.dsl


import scala.util.Try

import fa._

// TODO : Deprecate / cleanup

object Yahoo {
/*

  def sv(dm: DenseMatrix[Double]) =
    breeze.linalg.csvwrite(new File("text.txt"), dm, separator = ' ')
*/

  def main(args: Array[String]) {



/*
    import scala.collection.mutable

    val s = mutable.Map[String, mutable.Map[Price, mutable.Seq[Int]]]()

    val tm = mutable.Map[Int, mutable.Seq[Poll]]()

    bs foreach {
      p =>

        if (!tm.contains(p.time)) tm(p.time) = mutable.Seq(p)
        else {tm(p.time) = tm(p.time) ++ mutable.Seq(p)}


        val windowGen = s.get(p.sym)
        if (windowGen.isEmpty) {
          s(p.sym) = mutable.Map(Price(p.ask, p.bid) -> mutable.Seq(p.time))
        }
        else {
          val ps = windowGen.get
          val priceI: Price = Price(p.ask, p.bid)
          val pwin = ps.get(priceI)
          if (pwin.isEmpty) ps(priceI) = mutable.Seq(p.time)
          else {
            ps(priceI) = ps(priceI) ++ mutable.Seq(p.time)
          }
        }

    }


    val pt = gbk(baa)
      // mapvalues policy.

    val gp = pt.toList.map{
      case (k,v) =>
        k -> {

          val gv = gbk(v).toList
          .map{case (x,y) => x -> y.toList}

          val ptl = gv.length

          val fld = Iterator.tabulate(ptl/2){
            i =>
              val j = ptl - i - 1
              val x = gv(i)
              val y = gv(j)
              (i -> j) -> (x -> y)
          }

          fld.take(1).foreach{println}

          gv
        }
    }
*/

/*
    import breeze.linalg._
    import breeze.plot._


    val rn: DenseMatrix[Double] = DenseMatrix.rand(200,200)

    val rnn: DenseMatrix[Double] = DenseMatrix.rand(4,200)

    val f2 = Figure()
    f2.subplot(0) += image(rn)
    f2.saveas("image.png")*/
/*
    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0,1.0)
    p += plot(x, x :^ 2.0)
    p += plot(x, x :^ 3.0, '.')
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    f.saveas("lines.png") // save current figure as a .png, eps and pdf also supported
*/

  }

}
