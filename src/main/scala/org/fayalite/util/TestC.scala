package org.fayalite.util

import java.awt.event.{KeyEvent, KeyListener, WindowEvent}
import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame

import com.github.sarxos.webcam.Webcam

import scala.collection.mutable
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
    setPreferredSize(new Dimension(800, 600))
    setEnabled(true)
    pack()
    setVisible(true)
  }

  /**
    * Required for direct draws to buffer in func loop below
    *
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

  //Single graphics object available for
  //             drawing onto
  var draw: (Graphics => Unit) = (g: Graphics) => {
    g.setColor(Color.black)
    g.drawRect(0, 0, 800, 600)
    g.setColor(Color.white)
  }


  /**
    * Accumulate functions to execute in draw
    * order beginning with original instantiation
    * @param d : Draw func to add
    */
  def update(d: (Graphics => Unit)) = {
    val oldDraw = draw
    val newDraw = (g: Graphics) => {
      oldDraw(g)
      d(g)
    }
    draw = newDraw
  }

  /**
    * Event process loop for drawing
    *
    * @return : Future of draw loop for exception handling
    */
  def start() = F {
    while(true) {
      val bs = getBufferStrategy
      val g = bs.getDrawGraphics
      draw(g)
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

class CharMem() {

  val h = new mutable.HashMap[String, BufferedImage]()

  def get(s: String) = {
    h.getOrElseUpdate(s, {
      val b = new BufferedImage(25, 29, BufferedImage.TYPE_INT_ARGB)
      val bg = b.createGraphics()
      bg.setBackground(Color.BLACK)
      bg.setColor(Color.WHITE)
      bg.setFont(new Font("TimesRoman", Font.PLAIN, 27))
      bg.drawString(s, 1, 23)
      b
    })
  }

  val b = get("&")
  val outputfile = new File("image.jpg")
  ImageIO.write(b, "jpg", outputfile)

}

object TestC {
  /*

    val webcam = Webcam.getDefault()
    webcam.open()
  */

  import rx._
  import rx.ops._


  class FrameInit {

    val cm = new CharMem()

    val f = new FFrame()

    class Listen extends KeyListener {
      override def keyTyped(e: KeyEvent): Unit = {}

      override def keyPressed(e: KeyEvent): Unit = {
        val img = cm.get(e.getKeyChar.toString)
        f.draw
      }

      override def keyReleased(e: KeyEvent): Unit = {}
    }

    val l = new Listen()
    f.addKeyListener(l)
    f.init()
    f.start()
  }

  def main(args: Array[String]) {

    val f = new FrameInit()


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
