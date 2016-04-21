package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.LatCoord
import org.scalajs.dom.raw.MouseEvent

/**
  * Created by aa on 4/20/2016.
  */
trait InputHelp {


  implicit class MouseHelp(me: MouseEvent) {

    def tileCoordinates(tileSize: Int) = {
      val x = (me.clientX / tileSize).toInt * tileSize
      val y = (me.clientY / tileSize).toInt * tileSize
      LatCoord(x,y)
    }

  }
}
