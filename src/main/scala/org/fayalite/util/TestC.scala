package org.fayalite.util

import java.awt.event.WindowEvent
import java.awt._
import javax.swing.JFrame

import com.github.sarxos.webcam.Webcam

import scala.concurrent.Future


/**
  * Same as regular frame but with irritating
  * init methods wrapped up and a fix for being
  * able to draw directly to the buffer and
  * interface with it directly through a functional
  * process loop
 *
  * @param name : Your tag for the window
  */
class FFrame(name: String = "fayalite") extends JFrame(name) {

  /**
    * Does the gross stuff to get you a frame
    */
  def init() = {
    setBackground(Color.BLACK)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setEnabled(true)
    pack()
    setVisible(true)
  }

  /**
    * Required for direct draws to buffer in func loop below
    * @return : Guaranteed buffer strategy
    */
  override def getBufferStrategy = {
    val bs = super.getBufferStrategy
    if (bs == null) {
      super.createBufferStrategy(4)
      super.getBufferStrategy
    } else bs
  }

  def graphics = getBufferStrategy.getDrawGraphics

  import fa._

  /**
    * Event process loop for drawing
    * @param proc : Single graphics object available for
    *             drawing onto
    * @return : Future of draw loop for exception handling
    */
  def start(proc: (Graphics => Unit)) = F {
    while(true) {
      val bs = getBufferStrategy
      val g = bs.getDrawGraphics
      proc(g)
      g.dispose(); bs.show()
    }
  }

}

/**
  * Creates a fullscreen window with a workaround
  * to not minimize to the background, flickers a
  * little on context change but whatever, beats alternative
  *
  * @param windowId: String of 0,1,2 etc. corresponding to monitor id
  */
class FullScreenFrame(windowId: String) extends FFrame(
  name = windowId
) {
  override def processWindowEvent (we: WindowEvent): Unit = {
    setExtendedState (getExtendedState | Frame.MAXIMIZED_BOTH)
  }

  var graphicsDevice: GraphicsDevice = null

  override def init() = {
    setExtendedState(getExtendedState)
    setAlwaysOnTop(true)
    setUndecorated(true)
    super.init()
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
      case q if q.getIDstring.contains(windowId) => q.setFullScreenWindow(this)
        graphicsDevice = q
      case _ =>
    }
  }

}



object TestC {
/*

  val webcam = Webcam.getDefault()
  webcam.open()
*/

  def main(args: Array[String]) {

    val f = new FFrame()

    f.init()

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
