package org.fayalite.util.dsl

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Put stuff in here that doesn't easily go elsewhere but needs
  * to be somewhere.
  */
trait CommonJunk {

  def intTime = System.currentTimeMillis().toInt

  /**
    * Pretty obvious what it is, the operating system name.
    * Pattern match on stuff like contains 'Win' or 'Mac' if
    * you need, or use google to find all results this returns
    * depending on common system types
    */
  val osName = System.getProperty("os.name")

  /**
    * A friendly current time for S3 logs / whatever
    * @return Underscore delimited file-system friendly time
    */
  def currentTime = {
    val today = Calendar.getInstance().getTime
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss")
    minuteFormat.format(today)}

  /**
    * Filesystem friendly string representation ofjav
    * currentDay for serialization / day key checking
    * @return : String of exact day
    */
  def currentDay = {
    val today = Calendar.getInstance().getTime
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd")
    minuteFormat.format(today)
  }

  def homeDir = System.getProperty("user.home")

  val currentDir = new java.io.File(".").getCanonicalPath + "/"

  implicit class AnyDump(a: Any) {
    def p = println(a)
  }



}
