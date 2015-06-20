package org.fayalite.util


/**
 * Stuff that is too generic to make a tiny stub for.
 */
object Common {

  // TODO: NOT THIS. Anything but this. Should be from a UI based config in browser
  val home = System.getProperty("user.home")
  val SPARK_HOME = s"$home/Documents/repo/spark-dynamic/dist/"
  val currentDir = new java.io.File(".").getCanonicalPath + "/"

}
