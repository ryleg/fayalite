package org.fayalite.sjs.meta

import org.fayalite.sjs.Schema.LatCoord
import org.fayalite.sjs.canvas.CanvasBootstrap
import org.fayalite.sjs.comm.XHR
import org.fayalite.sjs.input.InputBootstrap

object MetaBootstrap {
  def init(): Unit = {

    XHR.requestTopLevelFiles{
      f =>
        println("Attempting mkWord", f.head)
        val coord: LatCoord = LatCoord(CanvasBootstrap.minSize * 5, CanvasBootstrap.minSize * 5)
        InputBootstrap.wordResolveType(coord) = "Folder"
        InputBootstrap.mkWord(f.head, coord)
    }


  }

}
