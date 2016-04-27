package org.fayalite.ml.yahoo.finance

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import fa._

import com.github.tototoshi.csv.CSVReader

/**
  * Yahoo historical CSVs per company
  */
object YahooParse {
  val hidden = new File(".hidden")
  val yahooCompanyCSVs = new File(hidden, "yahoo")
  def getRows = {
   yahooCompanyCSVs.listFiles.map{
     z => readLines()
   }
  }

}

/**
  * Created by aa on 4/20/2016.
  */
object TensorBuild {

  case class TimeIdentifiedRow(date: Date, unparsedDat: Seq[Int])

  val h = new File(".hidden")

  val hqp = new File(h, "yahoo2")
  val hqp3 = new File(h, "yahoo3")
  hqp3.mkdir()
  def main(args: Array[String]): Unit = {

    val j = hqp.listFiles().map{
      z =>
        val jq = readLines(z).toSeq.map{_.split("\t").toSeq.map{_.toInt}}
        jq
    }

    val all = j.flatten.flatten

    val gb = all.groupBy{
      z =>
        z
    }.map{case (x,y) => x -> y.length}
      .toSeq.sortBy{_._2}.reverse.map{_._1}.zipWithIndex.toMap
 // need to eliminate -1 parities later
    val gbp = j.map{
      _.map{
        _.map{
          i =>
            gb.get(i).get
        }
      }
    }

    gbp.zip(hqp.listFiles()).foreach{
      case (c, f) =>
        val newlns = c.map{
          _.mkString(",")
        }
        writeLines(new File(hqp3, f.getName).getCanonicalPath, newlns)
    }


  }
  def build = {
      val dt = new SimpleDateFormat("yyyy-MM-dd")
    val hp = new File(h, "yahoo")
//    val nt = hqp.listFiles().toSet
    hqp.mkdir()
    val faa = hp.listFiles().map {
     f =>
       val cmp = f.getName
     //  println("Trying " + cmp)
       val ztz = T {
         val hd = readCSV(f.getAbsolutePath)
         val stuff = hd.tail.filter { z => z.nonEmpty }.filter {
           j => j(0) != "" && j(0) != "Date"
         }.map {
           q =>
             //  println(cmp, q)
             val at = T {
               dt.parse(q(0))
             }
             if (at.isFailure) {
               println(cmp, q, hd)
             }
             val tp = at.get
             val strangeCase = q(q.length-2)
             TimeIdentifiedRow(tp, q.tail.map {
               ss =>
                 if (ss == strangeCase) ss.toInt else
                 (ss.toDouble * 1e6).toInt
             })
         }

         val firstRow = stuff.head
         val t0 = (firstRow.date.getTime / (1e5).toLong).toString
         val r0 = firstRow.unparsedDat.map {
           _.toString
         }
         val zro = Seq(t0) ++ r0
         val newstuff = stuff.tail.zipWithIndex.map {
           case (rr, ridx) =>
             val neighbor = stuff(ridx)
             val thisDate = rr.date.getTime
             val previousDate = neighbor.date.getTime
             val dtz = -1 * (thisDate - previousDate) / 1e5.toLong
         //    println(thisDate, previousDate, dtz)
             val dvs = rr.unparsedDat.zip(neighbor.unparsedDat).map { case (x, y) =>

          val nez =  x - y
      //  println(x,y,nez)
         nez
         }
             Seq(dtz.toString) ++ dvs.map {
               _.toString
             }
         }.toSeq
         val outp = new File(hqp, cmp).getCanonicalPath
         val cnt = Seq(zro) ++ newstuff
         writeLines(outp, cnt.map {
           _.mkString("\t")
         })
         println("wrote " + cmp)
         true
       }

       if (ztz.isFailure) println(
         "Failed on compnay " + cmp
       )
       true
   }
  }
}
