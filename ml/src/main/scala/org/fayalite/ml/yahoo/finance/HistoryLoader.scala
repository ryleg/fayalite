package org.fayalite.ml.yahoo.finance


import java.awt.Color
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.text.SimpleDateFormat
import javax.imageio.ImageIO

import breeze.linalg.{DenseMatrix, SparseVector}
import com.sun.jna.platform.unix.X11.Colormap
import com.xuggle.mediatool.{IMediaViewer, ToolFactory}
import fa._
import org.apache.spark.{SparkConf, SparkContext}
import org.fayalite.util.dsl.{JavaSerHelp, JavaSerHelpExplicit}

object JetColor {

  def interpolate(value: Double, y0: Double, x0: Double, y1: Double, x1: Double) = {
    (value - x0) * (y1 - y0) / (x1 - x0) + y0
  }
  def base(valu: Double) = {
    if (valu <= -0.75) 0D
    else if (valu <= -0.25) interpolate(valu, 0.0, -0.75, 1.0, -0.25)
    else if (valu <= 0.25) 1.0
    else if (valu <= 0.75) interpolate(valu, 1.0, 0.25, 0.0, 0.75)
    else 0.0
  }
  def red(gray: Double) = {
    base(gray - 0.5)
  }
  def green(gray: Double) = {
    base(gray)
  }
  def blue(gray: Double) = {
    base(gray + 0.5)
  }

  def jetColor(d: Double) = {
    new Color((red(d) * 255).toInt,
      (green(d) * 255).toInt,
      (blue(d) * 255).toInt,
      255)
  }

}

/**
  * Created by aa on 7/2/2016.
  */
object HistoryLoader {

  implicit class GBK[T, V](s: Seq[(T, V)]) {
    def groupByKey() = {
      s.groupBy(_._1).map{
        case (x,y) => x -> y.map{_._2}
      }
    }
  }

  implicit class SerExt[T](t: T) {
    def ser(f: String) = {

    JavaSerHelpExplicit.serialize[T](t).writeAllTo(f)
    }
  }

  implicit class ByteWrite(byteArray: Array[Byte]) {
    def writeAllTo(filename: String) = {
      val bos = new BufferedOutputStream(new FileOutputStream(filename))
      Stream.continually(bos.write(byteArray))
      bos.close() // You may end up with 0 bytes file if not calling close.
    }
  }

  case class Observe(date: String, open: Double)
  case class StockImage(company: Int, image: Seq[Observe])
  case class CompanyPrice(company: Int, price: Double)



  def brokenXuggleTest = {
    // default time is microseconds, 60~ fps is ~15000L microseconds
    var nextFrameTime = 0L

    val img = createImage(320, 200)

    val writer = ToolFactory.makeWriter(".imgs.mov")
    import com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT
    import java.util.concurrent.TimeUnit.MILLISECONDS

    val videoStreamIndex = 0
    val videoStreamId = 0
    val frameRate = DEFAULT_TIME_UNIT.convert(15, MILLISECONDS)
    val width = 320
    val height = 200

    writer.addListener(ToolFactory.makeViewer(
      IMediaViewer.Mode.VIDEO_ONLY, true,
      javax.swing.WindowConstants.EXIT_ON_CLOSE))

    writer.addVideoStream(videoStreamIndex, videoStreamId,
      width, height)

    import fa._

    def vid(frame: BufferedImage) = {
      writer.encodeVideo(videoStreamIndex, frame, nextFrameTime,
        DEFAULT_TIME_UNIT)
      nextFrameTime += 15000L
    }

    (0 until 60).foreach{
      z =>
        println("vid", z)
        vid(img)
    }

  }
  def main(args: Array[String]): Unit = {

    import org.fayalite.util.dsl.JavaSerHelpExplicit.deserialize
    import java.nio.file.{Files, Paths}

    val byteArray = Files.readAllBytes(Paths.get("data\\denseT"))

    val ray = deserialize[Array[Array[Double]]](
      byteArray,
      Thread.currentThread().getContextClassLoader
    )

    val s = ray.length*ray.head.length
    println(s)


    val m = ray.flatMap{_.distinct}.distinct.sorted.zipWithIndex.toMap
    val mx = 255*255*255
    val mx0 = m.size
    val boost = mx / mx0

    println(mx, mx0, boost)

    val prnt = ray.sortBy{_.max}
  // ffmpeg -f image2 -r 1/5 -i img%03d.png -c:v libx264 -pix_fmt yuv420p out.mp4

    def exportImg(prnt: Array[Array[Double]], fnm: String) = {
      val img0 = createImage(ray.length, ray.head.length).black
      prnt.zipWithIndex.foreach { case (z, w) =>
        z.zipWithIndex.foreach {
          case (j, q) =>
            val i = m(j) * boost
            /*          val major = (i/255*255) % 255
          val minor = (i/255) % 255
          val core = i % 255*/
            val c = JetColor.jetColor(m(j).toDouble / mx0)
            //val c = new Color(core, minor, major) //, ,
            img0.setRGB(w, q, c.getRGB)
        }
        if (w % 500 == 0) println(w)
      }
      println("Saving")
      val ri = img0.asInstanceOf[RenderedImage]
      println("render")
      val fi = new java.io.File(fnm)
      ImageIO.write(ri, "JPG", fi)
    }

   // import org.fayalite.util.dsl.JavaSerHelp.serialize

  //  serialize(tt).writeAllTo("tt")



    // try mod9prime graphop
    // color rgb, 3space unique double -> 3index map
/*



    //val bd = img0.getAllData
    ray.zipWithIndex.foreach{
      case (z, i) => z.zipWithIndex.foreach{
        case (w, j) =>
            img0.setRGB()
      }
    }

*/


    /*
    val dm = DenseMatrix.create(ray.size, ray.head.size, ray.flatten)
    import breeze.linalg._
    val mx0 = max(dm)
    val mn0 = min(dm)

    dm.mapValues()

    import breeze.plot._

    val f2 = Figure()

    // val rnd = DenseMatrix.rand(200,200)

    val image1: Series = image(dm)
    f2.subplot(0) += image1
    f2.saveas(".image.png")
*/

  }

  def twp = {

    val files: Seq[File] = new File(new File("data"), "historical")
      .listFiles()
      .toSeq

    val companyId = files.map {
      _.getName
    }.sorted.zipWithIndex.toMap

    println("parsing")


    val images = files
      .map { z =>
        // println("fpath", z.getCanonicalPath)
        val c = readCSV(z.getAbsolutePath)
        val observe = c.tail.map {
          l =>
            Observe(l(0), Math.log(l(1).toDouble))
        }
        StockImage(companyId(z.getName), observe)
      }

    println("transposing")

    val numCompanies: Int = files.length

    val maxNumDates = images.map {
      _.image.length
    }.max
    println("maxNumDates", maxNumDates)

    val min = images.map(_.image.map(_.open).min).min
    val max = images.map(_.image.map(_.open).max).max

    println("min", min)
    println("max", max)

    val dateLookup = images.flatMap{_.image.map{_.date}.distinct}.distinct
        .sorted
      .zipWithIndex.toMap

    println("densifying")

    val denseT = Array.fill(companyId.size)(Array.fill(dateLookup.size)(0D))

    images.foreach{
      s =>
        val rowX = s.company
        s.image.foreach{
          o =>
            denseT(rowX)(dateLookup(o.date)) = o.open
        }
    }

    denseT.ser(new File("data\\denseT").getAbsolutePath)

    /*
        val transposeByDate = images.flatMap { i =>
          i.image.map {
            z =>
              z.date -> CompanyPrice(i.company, z.open)
          }
        }.groupByKey().toSeq.sortBy {
          _._1
        }.map {
          _._2
        }.zipWithIndex
    */

/*
    val conf = new SparkConf()
    conf.setMaster("local[1]")
    conf.setAppName("local[1]")
    val sc = new SparkContext(conf)

    sc.parallelize(denseT.toSeq, 20).saveAsObjectFile("data\\denseT")
*/

    import fa._

    def other = {

      println("matrixing")

      val aro = denseT.flatten



      val img = createImage(numCompanies, maxNumDates).black

      var jj = 0
/*      val arrcum = Array.fill(101)(0)
      transposeByDate.foreach {
        case (z, w) =>
          z.foreach {
            cp =>
              val d = (cp.price - min) / (max - min)
              jj += 1
              arrcum((d * 100).toInt) += 1
              if (jj % 10000 == 0) {
                println(arrcum.toSeq)
              }

              val c = new Color((red(d) * 255).toInt,
                (green(d) * 255).toInt,
                (blue(d) * 255).toInt,
                255)
              img.setRGB(cp.company, w, c.getRGB)
          }
      }

      img.save(".test.png")

      println("encoding")*/
    }




  }

}
