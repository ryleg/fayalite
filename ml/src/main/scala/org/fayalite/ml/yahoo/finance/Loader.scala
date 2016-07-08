package org.fayalite.ml.yahoo.finance

import java.awt.{Color, Graphics}
import java.awt.color.ColorSpace
import java.awt.image.{BufferedImage, DataBufferByte, RenderedImage}
import java.io.{File, StringReader}
import java.text.SimpleDateFormat
import java.util.Date
import javax.imageio.ImageIO

import fa._
import org.apache.spark.{SparkConf, SparkContext}
import org.fayalite.ml.yahoo.finance.HistoryLoader.{Observe, StockImage}

/**
  * Created by aa on 7/7/2016.
  */
object Loader {

    def saveJPG(bi: BufferedImage)(path: String) = {
      val ri = bi.asInstanceOf[RenderedImage]
      val fi = new java.io.File(path)
      ImageIO.write(ri, "JPG", fi)
    }

  implicit class SaveAsExt(sv: Seq[String]) {
    def saveAsTextFile(output: String) = {
      val svWithNewlines = sv.map{_ + "\n"}
      scala.tools.nsc.io.File(output).writeAll(svWithNewlines:_*)
    }
  }
  implicit class CountByValueLike[K](sv: Seq[K]) {
    def countByValue() = {
      sv.map{_ -> 1L}.reduceByKey{_ + _}
    }
    def mapWithIndexPrinter[B](f: K => B, divModBy: Int = 500) = {
      sv.zipWithIndex.map {
        case (x, i) => if (i % divModBy == 0) println(i)
          f(x)
      }
    }
    def foreachWithIndexPrinter(f: K => Unit, divModBy: Int = 500) = {
      sv.zipWithIndex.foreach {
        case (x, i) => if (i % divModBy == 0) println(i)
          f(x)
      }
    }


    def zipMap = sv.zipWithIndex.toMap
    def mapMax[B](f: K => B)(implicit ord: Ordering[B]) = sv.map(f).max
    }

  implicit class ReduceByKey[K,V](sv: Seq[(K,V)]) {
    def reduceByKey(f: (V,V) => V) = {
      sv.groupBy{_._1}.map{
        case (k, seqkv) => k -> seqkv.map{_._2}.reduce(f)
      }
    }
  }

  case class HistoricalObservation(
                                    date: Date,
                                    open: Double,
                                    high: Double,
                                    low: Double,
                                    close: Double,
                                    volume: Int,
                                    adjustedClose: Double
                                  )

  val yahooDateFormat = "yyyy-MM-dd"

  val dateFormat = new SimpleDateFormat(yahooDateFormat)

  case class CompanyHistory(company: String, history: Seq[HistoricalObservation])

  def load() = {

    val res = new File(new File("data"), "historical").listFiles()
      .toSeq
      .map{
        fnm =>
          val a = readCSVFromFile(fnm)
          val observe = a.tail
            .filter{z => z.length > 5}
            .map {
              l =>
                HistoricalObservation(
                  dateFormat.parse(l.head),
                  l(1).toDouble,
                  l(2).toDouble,
                  l(3).toDouble,
                  l(4).toDouble,
                  l(5).toInt,
                  l(6).toDouble
                )
            }
          CompanyHistory(fnm.getName, observe)
      }



    val dateCBV = res.flatMap{_.history.map{_.date}}.countByValue()

    val validDates = dateCBV.filter{_._2 > 1500}.map{
      _._1.getTime
    }.toSeq

    validDates.map{_.toString}.saveAsTextFile(".validdates")

    val mina = validDates.min
    val min = validDates.min/100000L
    val max = (validDates.max - min)/100000L

    val remaining = res.filter{_.history.forall{z =>
      z.open < 254 && z.high < 254 && z.close < 254}}
   // val raz = Array.fill(max.toInt)(Array.fill(remaining.length)(0D))

    val img = createImage(remaining.length, max.toInt).black


    val cid = remaining.sortBy{_.history.mapMax{_.open}}.map{_.company}.zipMap

    val volume = remaining.flatMap{_.history.map{_.volume}}
    val vmax = volume.max
    val vmin = volume.min

    println("Vminmx " + vmax + " " + vmin)

    remaining.foreach{
     c =>
        c.history.foreach {
          h =>
            val offset = (h.date.getTime/100000L - min).toInt
            val rgb = new Color(h.open.toInt, h.high.toInt, h.close.toInt,
              {{(h.volume - vmin).toDouble/vmax}*255}.toInt).getRGB
            img.setRGB(cid(c.company), offset, rgb)
        }
    }

    println("Saving")
    img.save(".c243.png")

/*
    println("numBadDates " + badDates)
    dateCBV.toSeq.sortBy{_._1.getTime}.map{
      case (d, l) => dateFormat.format(d) + " " + l
    }.saveAsTextFile(".dates.txt")
*/


  }
  def main(args: Array[String]): Unit = {
    load()
  }

}
