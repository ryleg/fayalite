package org.fayalite.util.img

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

/**
  * Created by aa on 3/17/2016.
  */
trait ImageHelp {

  def readImg(f: String) = {
    val image = ImageIO.read(
      new File(f))
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

}
