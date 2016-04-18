package org.fayalite.util.img

import java.awt.image.{DataBuffer, Raster}

/**
  * Image related test utils // more experimental than
  * ImageUtil
  */
object AuxImageUtil {

  /**
    * This was for testing object level array copy ( for
    * setting RGB stuff ) / direct
    * modification of BufferedImage stuff
    * You can use the setter methods here
    * but they're not as fast as direct array ops
    * @param x : width
    * @param y : height
    * @return : Raster for interactions with buffered images
    */
  def mkRaster(x: Int, y: Int) = {
    Raster
      .createPackedRaster(
        DataBuffer.TYPE_INT, x, y, 3, 8, null)
  }

}
