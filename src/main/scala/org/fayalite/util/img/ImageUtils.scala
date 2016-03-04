package org.fayalite.util.img

import java.awt.Color

import org.fayalite.ui.WebsocketPipeClient
import spray.can.websocket.frame.BinaryFrame

/**
  * Image / manipulation / conversion utils
  */
object ImageUtils {

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

  //TBI
  def createBinaryTestFrameRequestor() = {
    val cli = new WebsocketPipeClient()
    val img = createTestImage()
    //val frame = BinaryFrame(bufferedImageToByteString(img))
   // cli.sendFrame(frame)
  }

}
