package org.fayalite.util


/**
 * Stuff that is too generic to make a tiny stub for.
 */
object Common {

  // TODO: NOT THIS. Anything but this. Should be from a UI based config in browser
  val home = "/home/ubuntu/fayalite" //System.getProperty("user.home")
  val SPARK_HOME = s"$home/fayalite/"
  val currentDir = new java.io.File(".").getCanonicalPath + "/"

}
