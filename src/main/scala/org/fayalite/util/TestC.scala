package org.fayalite.util

import java.awt.event.WindowEvent
import java.awt._
import javax.swing.JFrame

import com.github.sarxos.webcam.Webcam


/**
  * Creates a fullscreen window with a workaround
  * to not minimize to the background, flickers a
  * little on context change but whatever, beats alternative
  * @param windowId: String of 0,1,2 etc. corresponding to monitor id
  */
class FullScreenFrame(windowId: String) extends JFrame("") {
  override def processWindowEvent (we: WindowEvent): Unit = {
    setExtendedState (getExtendedState | Frame.MAXIMIZED_BOTH)
  }
  var graphicsDevice: GraphicsDevice = null

  def init() = {
    setExtendedState(getExtendedState)
    setBackground(Color.BLACK)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setAlwaysOnTop(true)
    setUndecorated(true)
    setEnabled(true)
    pack()
    setVisible(true)
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
      case q if q.getIDstring.contains(windowId) => q.setFullScreenWindow(this)
        graphicsDevice = q
      case _ =>
    }
  }
  override def getBufferStrategy = {
    val bs = super.getBufferStrategy
    if (bs == null) {
      super.createBufferStrategy(4)
      super.getBufferStrategy
    } else bs
  }

  def graphics = getBufferStrategy.getDrawGraphics

}


object TestC {

  val webcam = Webcam.getDefault()
  webcam.open()

  def main(args: Array[String]) {

    }/*

    val frame = new

    while(true) {
      val bs = frame.getBufferStrategy
        val g = bs.getDrawGraphics
        val img = webcam.getImage
        val h = img.getHeight; val w = img.getWidth()
        val sw =  sd.getDisplayMode.getWidth
        val sh =  sd.getDisplayMode.getHeight
        val sbi = img.getSubimage(50, 50, w-150, h-50)

        g.drawImage(sbi, 0, 0, sd.getDisplayMode.getWidth, sd.getDisplayMode.getHeight, Color.BLACK, null)
        g.dispose(); bs.show()
        //   println("drew img.")
      }
*/



}
