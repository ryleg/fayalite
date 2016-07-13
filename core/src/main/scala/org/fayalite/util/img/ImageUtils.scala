package org.fayalite.util.img
import java.awt.image.{DataBufferByte, DataBufferInt, RenderedImage}
import javax.imageio.ImageIO

import akka.util.ByteString
import rx._

import scala.collection.Iterable
import scala.io.Source
import scala.util.{Failure, Success, Try}
import java.awt.image.BufferedImage
import java.awt.Color

/**
  * Image / manipulation / conversion utils
  */
object ImageUtils {


  /**
    * A set of demonstrative functions that show how to convert
    * canvas data from HTML5 ByteStrings received through a
    * websocket into java compatible images and back!
    */

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
  implicit class BufferedImageExtensions(bi: BufferedImage) {
    def save(path: String) = {
      val ri = bi.asInstanceOf[RenderedImage]
      val fi = new java.io.File(path)
      ImageIO.write(ri, "PNG", fi)
    }
  }
  def bufferedImageToByteString(bi: BufferedImage) = {
    val w = bi.getWidth
    val h = bi.getHeight
    val rgbaInt = bi.getRGB(0, 0, w, h, null, 0, w)
    val rgba = rgbaInt.flatMap{ b =>
      val c = new Color(b, true)
      Seq(
        c.getRed,
        c.getGreen,
        c.getBlue,
        c.getAlpha
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


  /**
    * Just an example to show how to use AWT
    * / Java2d Image api.
    *
    * @return : BufferedImage with some random string
    */
  def createTestImage() = {
    val width = 500
    val height = 500
    import java.awt.image.BufferedImage
    val image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    val g = image.createGraphics()
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, width, height)
    g.setColor(Color.white)
    g.drawString("yo " + scala.util.Random.nextString(10), 100, 100)
    image
  }

}
