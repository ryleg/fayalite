package org.fayalite.ui

import java.awt.image.{DataBufferByte, DataBufferInt, RenderedImage}
import javax.imageio.ImageIO;

import akka.util.ByteString
import org.fayalite.util.SparkReference
import rx._

import scala.collection.Iterable
import scala.io.Source
import scala.util.{Failure, Success, Try}

object ClientMessageToResponse {


  var allMsgs = ""

  val parsedMessage = Var("")

  import sun.awt.image.codec.JPEGImageEncoderImpl

  import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
  import java.awt.image.BufferedImage
  def bi2db(bi: BufferedImage) = {
    //val fos = new ByteOutputStream()
   // val j = new JPEGImageEncoderImpl(fos)
  //  j.encode(bi)
    //fos.close()
   // fos.getBytes
  }
  import java.awt.Color
//  val rgbArray = Array.fill(300*300)(0)

  def byteStringToUInt8ToRGBInt(bs : ByteString) = {
    val uint = bs.toIterable.map {
      b => b & 0xFF
    }.grouped(4).map {
      _.toList
    }.map {
      case List(r, g, b, a) => new Color(r, g, b, a).getRGB
    }
    uint.toArray
  }

  case class UIParams(width: Int, height: Int)

  object RGBI {
    def apply(bs: ByteString)(implicit uip: UIParams) = {
      new RGBI(byteStringToUInt8ToRGBInt(bs), uip.width, uip.height)
    }
  }
  class RGBI(rgba: Array[Int], width: Int, height: Int) {


    import java.awt.image.BufferedImage
    val image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    val g = image.createGraphics()
    image.setRGB(0, 0, width, height, rgba, 0, width)
    def save(path: String) = {
      val ri = image.asInstanceOf[RenderedImage]
      val fi = new java.io.File("adfsf.png")
      ImageIO.write(ri, "PNG", fi)
    }

    def byteString = bufferedImageToByteString(image)

  }

  def bufferedImageToByteString(bi: BufferedImage) = {
    val w = bi.getWidth
    val h = bi.getHeight
    val rgbaInt = bi.getRGB(0, 0, w, h, null, 0, w)
    val rgba = rgbaInt.flatMap{ b =>
      val c = new Color(b, true)
      Seq(
        c.getRed(),
        c.getGreen(),
        c.getBlue(),
        c.getAlpha()
      ).map{_.toByte}
    }
    ByteString(rgba)
  }


  implicit def byteStringToUInt8(bs : ByteString): IndexedSeq[Int] = {
    bs.map {
      b => b & 0xFF
    }
  }


  implicit def uInt8ToByteString(uint8: IndexedSeq[Int]) : ByteString = {
    ByteString(uint8.map{_.toByte}.toArray)
  }

def main(args: Array[String]) {


/*

  val dat = SparkReference.getSC.objectFile[ByteString]("dat").first

  val s : IndexedSeq[Int] = dat
  val ss : ByteString = s

  dat.slice(0, 10).foreach{println}
  println("dat")
  s.slice(0, 10).foreach{println}
  println(s.zip(ss).filter{case (x,y) => x != y}.length)
  println(s.length + " " + ss.length)
  ss.slice(0, 10).foreach{println}
*/

  /*
    val rgba = byteStringToUInt8ToRGBInt(dat)
    val rg = new RGBI(rgba, 460, 495)
    println(rg.byteString == dat)*/
  /*
  dat.toIterable.map {
    b => (b, b & 0xFF)
  }.toList.slice(0, 10).foreach{println}
//  val uint8 = byteStringToUInt8(dat)
*/
}
/*

  println(dat.length)


  println(uint.slice(0, 15))

  import java.awt.image.BufferedImage
  val image = new BufferedImage(460, 495, BufferedImage.TYPE_4BYTE_ABGR);
  val g = image.createGraphics()
  val w = image.getWidth
  val h = image.getHeight
  g.setBackground(Color.BLUE)
  g.setPaint ( Color.BLUE);
  g.fillRect ( 0, 0, w,h);
//  g.drawImage(image, null, 0, 0);
  g.setColor(Color.YELLOW)
  g.drawString("asdfzsd", 55, 55) // (x/300, y/300)

 // val argb =     image.getRGB(0, 0, w, h, null, 0, w);
//  val dbb = image.getRaster.getDataBuffer.asInstanceOf[DataBufferByte]
//   val db = dbb.getData.toList
 // val len = db.getSize

 // val of = (0 until len).toList.map{idx => db.getDataType}
    val argb = image.getRGB(0, 0, w, h, null, 0, w).toList
  println(argb.slice(0, 10))

  image.setRGB(0, 0, w, h, rgbs.toArray, 0, w)

  val rgba = argb.flatMap{ b =>
    val c = new Color(b, true);
    Seq(    c.getRGB,
      c.getRed(),
      c.getGreen(),
      c.getBlue(),
      c.getAlpha())
  }
  println(rgba.slice(0, 10))

  //   imag
 // e.setRGB(0, 0, w, h, msg, 0, w)
/*


    println("rgba " + rgba.slice(0, 10).toList)
    println("msg " + msg.slice(0, 10).toList)*/

    val ri = image.asInstanceOf[RenderedImage]
    val fi = new java.io.File("adfsf.png")
    ImageIO.write(ri, "PNG", fi)

}*/
  def moveToResponse(x: Int, y: Int) = {
/*
    var imgData=ctx.getImageData(0,0,c.width,c.height);
    // invert colors
    for (var i=0;i<imgData.data.length;i+=4)
    {
      imgData.data[i]=255-imgData.data[i];
      imgData.data[i+1]=255-imgData.data[i+1];
      imgData.data[i+2]=255-imgData.data[i+2];
      imgData.data[i+3]=255;
    }
    ctx.putImageData(imgData,0,0);*/
    import java.awt.image.BufferedImage
    val image = new BufferedImage(460, 495, BufferedImage.TYPE_INT_ARGB);
    val g = image.createGraphics()
    g.setBackground(Color.red)
    //g.drawImage(image, null, 0, 0);
    g.setColor(Color.white)
    g.drawString("yo", x, y) // (x/300, y/300)
    val w = image.getWidth
    val h = image.getHeight
    val argb =     image.getRGB(0, 0, w, h, null, 0, w);

    val rgba = argb.flatMap{ b =>
        val c = new Color(b, true);
        Seq(c.getRed(),
   c.getGreen(),
 c.getBlue(),
 c.getAlpha())
    }.mkString(",")

    val ri = image.asInstanceOf[RenderedImage]
    val fi = new java.io.File("/Users/ryle/adfsf.jpg")
    ImageIO.write(ri, "JPEG", fi)

    rgba
  }

  def parse(msg: String) : String = {
    val atrep = Try {
      val sm = msg.split(",").toList
      println("parse " + sm)
//      val cod = sm.headOption
      if (sm.length > 1) {
        sm.head match {
          case "move" =>
            val x = sm(1).toInt
            val y = sm(2).toInt
            //    println("move PARSE" + x + " " + y)
            "putpixel," + moveToResponse(x, y)
          case _ =>
            //println(" not move")
            "failed parse"
        }
      }
      else "fail"
    } match {
      case Success(x) => x
      case Failure(e) => e.printStackTrace(); "Failedtrypasre"
    }
    atrep
  }

  def startWatch() = {
    Obs(Duplex.a) {
      println("startWatch watch")
      val curMsg = Duplex.a()
      //allMsgs += curMsg
      val pm = parse(curMsg)
      println(pm)
     // pm.map { p => parsedMessage() = p

    }
  }

}
