package org.fayalite.util.img

import java.awt.{Color, Graphics}
import java.awt.image.{BufferedImage, DataBufferByte, DataBufferInt, RenderedImage}
import java.io.File
import javax.imageio.ImageIO

/**
  * Created by aa on 3/17/2016.
  */
trait ImageHelp {

  def getHueColor(power: Double) = {
    val H = power
    val S = 0.9D
    val B = 0.9D
    Color.getHSBColor(H.toFloat, S.toFloat, B.toFloat)
  }

  def readImg(f: String) = {
    val image = ImageIO.read(
      new File(f))
    image
  }

  /**
    * Simple and straightforward image
    * @param width : Pixel width
    * @param height : Pixel height
    * @return Buffered Image
    */
  def createImage(width: Int, height: Int) = {
    import java.awt.image.BufferedImage
    val image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
    image
  }



  implicit class BuffImageExt (bi: BufferedImage ) {
    def draw(g: Graphics, x: Int, y: Int) = {
      g.drawImage(bi, x, y, bi.getWidth, bi.getHeight,null)
    }
    def sizeDuplicate = {
      createImage(bi.getWidth, bi.getHeight)
    }

    def black = {
      val g = bi.createGraphics()
      g.setColor(Color.BLACK)
      g.fillRect(0, 0, bi.getWidth, bi.getHeight)
      g.dispose()
      bi
    }

    val hasAlphaChannel = bi.getAlphaRaster() != null
    val pixelLength = if (hasAlphaChannel) 4  else 3

    def getAllData = bi
      .getRaster
      .getDataBuffer
      .asInstanceOf[DataBufferByte]
      .getData


    def save(path: String) = {
      val ri = bi.asInstanceOf[RenderedImage]
      val fi = new java.io.File(path)
      ImageIO.write(ri, "PNG", fi)
    }
  }

}
