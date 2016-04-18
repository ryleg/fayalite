package org.fayalite.ml.yahoo.finance

import fa._

/**
  * Created by aa on 4/18/2016.
  */
object ImageDecode {

    def main(args: Array[String]): Unit = {
      val img = readImg(".tesyahoo.png")
      val data = img.getAllData // just for demonstrating conversion
      // you can also just get the bytes from the file directly.

      println("data length" + data.length)
      val h = img.getHeight
      println("h w " + h + " " + img.getWidth)

      val m = data.grouped(4*img.getWidth).toArray



    }
}
