package org.fayalite.util

import java.awt.event._
import java.awt._
import java.awt.image.BufferedImage
import java.io.{Reader, BufferedReader, File}
import javax.imageio.ImageIO
import javax.swing.{JButton, JPanel, JLabel, JFrame}

import ammonite.ops.Path
import ammonite.repl.{Ref, Storage}
import com.github.sarxos.webcam.Webcam
import org.fayalite.repl.REPLManagerLike
import org.scalatest.FlatSpec
import org.scalatest.selenium.Chrome
import org.scalatest.selenium.WebBrowser.go

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
    *
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
  /**
    * This causes a little flicker on context
    * switching but it restores the max state, otherwise
    * the window will just dissapear.
    *
    * @param we : Some window event, like the user clicked on a different
    *           monitor.
    */
  override def processWindowEvent (we: WindowEvent): Unit = {
    setExtendedState (getExtendedState | Frame.MAXIMIZED_BOTH)
  }

  /**
    * This is for assigning the monitor on discovery
    * in case you ever need to use the reference
    * to the monitor device for grabbing info about it.
    */
  var graphicsDevice: GraphicsDevice = null

  override def init() = {
    setExtendedState(getExtendedState)
    setAlwaysOnTop(true)
    setUndecorated(true)
    super.init()
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
      case q if q.getIDstring.contains(windowId) =>
        q.setFullScreenWindow(this) // This triggers fullscreen
        graphicsDevice = q // for grabbing window info.
      case _ => // Assign errors through overrides if requested.
    }
  }

}

/**
  * Drawing characters excessively through the
  * drawString function wastes memory and is irritating
  * to manage.
  *
  * Instead we intead to route all chracter buffering
  * through either BufferedImage hashes or VolatileImage
  * stateless draws.
  *
  */
class SymbolRegistry() {

  // For storing prerendered characters and/or unicode-like
  // strings, basically anything that would go through drawString
  // and needs buffering/mapping in some capacity
  val h = new mutable.HashMap[String, BufferedImage]()

  /**
    * Grab the chosen unicode-like string for the Graphics2d
    * drawString call.
    *
    * @param s : Some string, typically a single character that will
    *          fit inside the typical draw window.
    *          Override / edit to get
    * @return : Small tile containing your draw
    */
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

  /**
    * Save a tile to a file for viewing or other usage.
    *
    * @param s : String, see get for doc
    * @param f : String
    * @return
    */
  def exportTile(s: String, f: String) = {
    val b = get(s)
    val q = new File(f)
    ImageIO.write(b, "jpg", q)
  }

}

import fa._

/**
  * Just a really simple button
  *
  * @param title : Name of button
  * @param action : When someone clicks on it, don't
  *               expect much information here, this is
  *               simple
  */
class Button(title: String, action: () => Unit) {
  val jButton = new JButton(title)
  jButton.addMouseListener(new MouseListener{
    override def mouseExited(e: MouseEvent): Unit = {}
    override def mouseClicked(e: MouseEvent): Unit = {
      action()
    }
    override def mouseEntered(e: MouseEvent): Unit = {}
    override def mousePressed(e: MouseEvent): Unit = {}
    override def mouseReleased(e: MouseEvent): Unit = {}
  })
}



class ToyFrame {
  import SwingManage._

  val f = toyFrame
  //   val f = new FrameInit()

  val jp = new JPanel(false)

  def addButton[T](s: String, f: => T) = {
    jp.add(
      new Button(s, () => f).jButton)
  }
  def ad(e: Component) = jp.add(e)

  val bigFont = new Font("monospace", Font.PLAIN, 20)
  def addTextInput[T](s: String) = {
    val ta = new TextArea(s, 1, 15, TextArea.SCROLLBARS_NONE)
    ta.setText(s)
    ta.setFont(bigFont)
    ta.setForeground(Color.WHITE)
    jp.add(ta)
    ta
  }


  f.add{jp}

  def finish() = {
    f.pack()
    f.setVisible(true)
    f.setSize(new Dimension(320, 600)) // Trick to force layout
  }
}


/**
  * Frame management for Swing
  */
object SwingManage {

  /**
    * Simple small button panel.
    *
    * @return : Frame
    */
  def toyFrame = {
    val f = new JFrame("Selenium Control Panel")
    f.setBackground(Color.BLACK)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setPreferredSize(new Dimension(150, 600))
    f.setEnabled(true)
    f
  }

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
