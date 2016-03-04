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
    val today = Calendar.getInstance().getTime
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss")
    minuteFormat.format(today)}

  def currentDay = {
    val today = Calendar.getInstance().getTime
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd")
    minuteFormat.format(today)
  }

  implicit class BufferedImageHelp(bi: BufferedImage) {
    def draw(g: Graphics, x: Int, y: Int) = {
      g.drawImage(bi, x, y, bi.getWidth, bi.getHeight,null)
    }
  }

  val ubuntuProjectHome = "/home/ubuntu/fayalite" //System.getProperty("user.home")
  val SPARK_HOME = s"$ubuntuProjectHome"
  val currentDir = new java.io.File(".").getCanonicalPath + "/"


}
