package org.fayalite.ui.j2d

import java.awt._
import java.awt.event._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{JButton, JFrame, JPanel}

import scala.collection.mutable



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

  def doDraw() = {
    draw(graphics)
    postDraw()
  }

  def postDraw() = {
    val bs = getBufferStrategy
    val g = bs.getDrawGraphics
    g.dispose(); bs.show()
  }

  /**
    * Event process loop for drawing
    *
    * @return : Future of draw loop for exception handling
    */
  def start() = F {
      while(true) {
        doDraw() // This can use a lot of cpu potentially if repeated
      }
      }

}
