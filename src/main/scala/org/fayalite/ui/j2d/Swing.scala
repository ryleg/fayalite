package org.fayalite.ui.j2d

import java.awt._
import java.awt.event._
import javax.swing.JPanel

class DrawPanel(var draw: (Graphics => Unit)) extends JPanel {
  override protected def paintComponent(g : Graphics) = {
    super.paintComponent(g)
    draw(g)
  }
}

//  val pane = frame getContentPane

object Swing {

  def screenSize = Toolkit.getDefaultToolkit.getScreenSize

  def main(args: Array[String]) {

    val m = new MouseListener {
      override def mouseExited(e: MouseEvent): Unit = {}

      override def mouseClicked(e: MouseEvent): Unit = {
        val p: Point = e.getPoint
        println("msclck " + p.x + " " + p.y)
        println(p.x < 215)
        println(p.y < 100)
        if (p.x < 215 && p.y < 100) {
        }
      }

      override def mouseEntered(e: MouseEvent): Unit = {}

      override def mousePressed(e: MouseEvent): Unit = {}

      override def mouseReleased(e: MouseEvent): Unit = {}
    }
  }
    /*
        val vi: VolatileImage = frame.createVolatileImage(100, 100)
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gc = ge.getDefaultScreenDevice().getDefaultConfiguration()
        def createVolatileImage(width: Int, height: Int, transparency: Int): VolatileImage = {
          val image = gc.createCompatibleVolatileImage(width, height, transparency)
          val valid = image.validate(gc)
          println("valid " + valid)
          if (valid == VolatileImage.IMAGE_INCOMPATIBLE)
          createVolatileImage(width, height, transparency)
          else image
        }

        var vimage = createVolatileImage(800, 600, Transparency.OPAQUE)

        var g: Graphics2D = null

        vimage.createGraphics()


        val w = 800
        val h = 600
        val t = Transparency.OPAQUE

        do {
          if (vimage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
            vimage = createVolatileImage(w, h, t)
          }
          println("Contents lost")
          Thread.sleep(500)
          Try {
            g = vimage.createGraphics()
            g.drawBytes(vb, 0, 0, 0, 0)
          }
          g.dispose()
        } while (vimage.contentsLost())

        $
  }
  */
}
