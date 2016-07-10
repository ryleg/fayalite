package org.fayalite.ml.yahoo.finance

import java.awt.{Color, Graphics}
import java.awt.color.ColorSpace
import java.awt.image.{BufferedImage, DataBufferByte, RenderedImage}
import java.io.{File, StringReader}
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.{Calendar, Date, GregorianCalendar}
import javax.imageio.ImageIO

import fa._
import org.apache.spark.{SparkConf, SparkContext}
import org.fayalite.ml.yahoo.finance.HistoryLoader.{Observe, StockImage}

import scala.util.Try

/**
  * Created by aa on 7/7/2016.
  */
object Loader {

    def saveJPG(bi: BufferedImage)(path: String) = {
      val ri = bi.asInstanceOf[RenderedImage]
      val fi = new java.io.File(path)
      ImageIO.write(ri, "JPG", fi)
    }

    def saveBMP(bi: BufferedImage)(path: String) = {
      val ri = bi.asInstanceOf[RenderedImage]
      val fi = new java.io.File(path)
      ImageIO.write(ri, "BMP", fi)
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

  case class EncodedHistoricalObservation(
                                         dayOffset: Int,
                                         open: Double,
                                         high: Double,
                                         low: Double,
                                         close: Double,
                                         volume: Int,
                                         adjustedClose: Double
                                         )
  case class EncodedCompanyHistory(
                                  companyId: Int,
                                  encodedHistory: Seq[EncodedHistoricalObservation]
                                  )

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

  println("loaded")

    val dateCBV = res.flatMap{_.history.map{_.date}}.countByValue()

    println("cbv")

    val validDates = dateCBV.filter{_._2 > 1500}.keySet

    println("validnlen " + validDates.size )
    println("reslen " + res.size )

    val day0 = validDates.minBy(_.getTime)
    val day0m = validDates.maxBy(_.getTime)
    val cl0 = new GregorianCalendar()
    val cl0m = new GregorianCalendar()
    cl0.setTime(day0)
    cl0m.setTime(day0m)

    def daysBetween(d2: Date, d1: Date) = {
      (d2.getTime - d1.getTime) / (1000 * 60 * 60 * 24)
    }.toInt

    val rng = daysBetween(cl0m.getTime, cl0.getTime)


    println(rng + " rng")

    val remaining = res.map{
      z => z.copy(history = z.history.filter{h => validDates.contains(h.date)})
    }

   // println("remaining length " + remaining.length)

 //   val img = createImage(remaining.length, rng + 1).black


    val cid = remaining.sortBy{_.history.mapMax{_.open}}.map{_.company}.zipMap


    println("Cidsize " + cid.size)
/*
    val volume = remaining.flatMap{_.history.map{_.volume}}
    val vmax = volume.max
    val vmin = volume.min

    println("Vminmx " + vmax + " " + vmin)
*/

    import HistoryLoader.SerExt

  val fn = remaining.map{
      z =>
        cid(z.company) -> z.history.map {
          h =>
            val clz = new GregorianCalendar()
            clz.setTime(h.date)
            val offset = daysBetween(clz.getTime, cl0.getTime)
            offset -> h.open
        }
    }

     fn.ser(".preimg")
/*

    remaining.foreachWithIndexPrinter{
     c =>
        c.history.foreach {
          h =>
            val clz = new GregorianCalendar()
            clz.setTime(h.date)
            val offset = daysBetween(clz.getTime, cl0.getTime)
            val vlz = { {h.volume.toDouble/vmax}*254}.toInt
/*            val rgb = new Color(
              Math.log(h.open).toInt, Math.log(h.high).toInt, Math.log(
                h.close).toInt,
              Math.log(vlz).toInt).getRGB*/
            val rgb = JetColor.jetColor(Math.log(Math.pow(h.open, 2)) / 30).getRGB
          //  val rgb = Math.log(Math.pow(h.open, 2)).toInt
            Try {
              img.setRGB(cid(c.company), offset, rgb)
            } match {
              case scala.util.Failure(e) =>
                println(offset, h.open, h.high, h.close, vlz)
              case _ =>
            }
        }
    }

    println("Saving")
    saveJPG(img)(".jca.jpg")
*/

/*
    println("numBadDates " + badDates)
    dateCBV.toSeq.sortBy{_._1.getTime}.map{
      case (d, l) => dateFormat.format(d) + " " + l
    }.saveAsTextFile(".dates.txt")
*/


  }

  def loadAs[T](f: String) = {
    import org.fayalite.util.dsl.JavaSerHelpExplicit.deserialize
    import java.nio.file.{Files, Paths}
    val byteArray = Files.readAllBytes(Paths.get(f))
    deserialize[T](
      byteArray,
      Thread.currentThread().getContextClassLoader
    )
  }

  def proc() = {
    import HistoryLoader.SerExt

    val dt = loadAs[Seq[(Int, Seq[(Int, Double)])]](".preimg")

    println(dt.size)

  }
  def main(args: Array[String]): Unit = {
   // load()
    proc()
  }

}
