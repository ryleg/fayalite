package org.fayalite.util.img

import java.io.File
import javax.imageio.ImageIO

/**
  * Created by aa on 3/17/2016.
  */
trait ImageHelp {

  def readImg(f: String) = {
    val image = ImageIO.read(
      new File(f))
  }

}
