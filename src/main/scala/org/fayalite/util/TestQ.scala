package org.fayalite.util

import java.awt._
import java.awt.event.{KeyEvent, KeyListener, WindowEvent}
import javax.swing.JFrame

/**
  * Created by aa on 2/1/2016.
  */
object TestQ {

  def main(args: Array[String]) {

    val frame = new JFrame("Selenium Control Panel")
    frame.setPreferredSize(new Dimension(500, 500))
    frame.setBackground(Color.BLACK)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setAlwaysOnTop(true)
    frame.setUndecorated(true)
    frame.setEnabled(true);
    frame.pack;
    frame.setVisible(true)

    val l = new KeyListener {

      def keyPressed(k: KeyEvent) = {
        println("key pressed " + k.getKeyChar)
      }

      def keyTyped(k: KeyEvent) = {
        println("Key typed " + k.getKeyChar)
      }

      def keyReleased(k: KeyEvent) = {
        println("Key released " + k.getKeyChar)
      }

    }

    frame.addKeyListener(l)

    var sd: GraphicsDevice = null
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
      case q if q.getIDstring.contains("2") => sd = q
      case _ =>
    }
    while (true) {
      val bs = frame.getBufferStrategy
      if (bs == null) {
        frame.createBufferStrategy(4)
      } else {
        val g = bs.getDrawGraphics
        //     g.drawImage(img, 0, 0, sd.getDisplayMode.getWidth, sd.getDisplayMode.getHeight, Color.BLACK, null)
        g.dispose();
        bs.show()
        //   println("drew img.")
      }

    }
  }
}
