package org.fayalite.ui.j2d

import java.awt._
import java.awt.event._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{JButton, JFrame, JPanel}

import scala.collection.mutable



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
