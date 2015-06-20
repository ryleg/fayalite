package org.fayalite.ui.io

import java.awt.Color
import java.awt.image.{RenderedImage, BufferedImage}
import javax.imageio.ImageIO

import akka.actor.ActorRef
import org.fayalite.ui.ImageUtils._
import org.fayalite.ui.{ImageUtils, WebsocketPipeClient}
import spray.can.websocket.frame.BinaryFrame

/**
 * In theory this could be used for piping image data between
 * client and JVMs.
 * In practice I don't even remember if it works. Honest docs whoa.
 */
object ImagePipe {

  val default = createTestImage()

  def createTestImage() = {
    val width = 500
    val height = 500
    import java.awt.image.BufferedImage
    val image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    val g = image.createGraphics()
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, width, height)
    g.setColor(Color.white)
    g.drawString("yo", 100, 100) // + scala.util.Random.nextString(10)
    image
  }

  def parseMessage(msg: String, ref: ActorRef) = {
    val sm = msg.split(",")
    val flag = sm.headOption
    flag match {
      case Some("move") =>
        val rest = sm.tail
        val (x, y) = rest.map{_.toInt % 500} match {
          case Array(x,y) => (x,y)
        }
        val g = default.createGraphics()
        g.drawString("wh", x, y)
        val frame = BinaryFrame(bufferedImageToByteString(default))
        ref ! frame
      case _ =>

    }
  }

}
