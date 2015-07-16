/*
package org.fayalite.ui

import java.awt.Color

import org.scalatest.FunSuite
import spray.can.websocket.frame.{BinaryFrame, TextFrame}


/**
 * Created by ryle on 11/9/2014.
 */

class TestImagePipeToBrowser extends FunSuite {

  test("Sending a red image with cyan yo in text to all browsers") {

    import WebsocketPipeClient._
    import ImageUtils._

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
    val cli = new WebsocketPipeClient()

   //   while (true) {
        val img = createTestImage()

        val frame = BinaryFrame(bufferedImageToByteString(img))
    //    Thread.sleep(3000)
        cli.sendFrame(frame)
   //   }
    }
}


*/
