package org.fayalite.ui.j2d

import java.awt._
import java.awt.event._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{JButton, JFrame, JPanel}

import scala.collection.mutable

/**
  * Just a really simple button
  *
  * @param title : Name of button
  * @param action : When someone clicks on it, don't
  *               expect much information here, this is
  *               simple
  */
class FButton(title: String, action: () => Unit) {
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

  val jp = new JPanel()

  def addButton[T](s: String, f: => T) = {
    jp.add(
      new FButton(s, () => f).jButton)
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
    f.setSize(new Dimension(900, 600)) // Trick to force layout
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

  }

}
