package org.fayalite.ml

object Main {

}


abstract class Image {

  def coherency: Double


}

trait StockImage extends Image {

}


abstract class Swap(val image: Image) {

  /**
    * Initiates a swap operation on the image
    */
  def initiateSwap : Unit

}
