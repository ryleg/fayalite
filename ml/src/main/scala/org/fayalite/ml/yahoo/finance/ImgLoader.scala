package org.fayalite.ml.yahoo.finance

import java.io.File

import com.sksamuel.scrimage.Image
import fa._

object ImgLoader {

  val path = new File(homeDir, "nerv")
  val train = new File(path, "train")
  val masks = readCSV(new File(path, "train_masks.csv").getCanonicalPath).tail


  def main(args: Array[String]) {

    val fMask = masks.head(2).split(" ").grouped(2).toSeq.map{
      case Array(pIdx, runLen) =>
        pIdx.toInt -> runLen.toInt
    }

    val first = new File(train, "1_1.tif")

    val img = Image.fromFile(first)
    val imgi = img.pixels.map{_.argb}.zipWithIndex

  }

}
