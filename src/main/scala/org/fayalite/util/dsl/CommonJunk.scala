package org.fayalite.util.dsl

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Created by aa on 2/18/2016.
  */
trait CommonJunk {

  def currentTime = {
    val today = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss")
    minuteFormat.format(today)}

  implicit class BufferedImageHelp(bi: BufferedImage) {
    def draw(g: Graphics, x: Int, y: Int) = {
      g.drawImage(bi, x, y, bi.getWidth, bi.getHeight,null)
    }
  }


}
