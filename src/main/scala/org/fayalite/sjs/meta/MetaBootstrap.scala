package org.fayalite.sjs.meta

import org.fayalite.sjs.Schema.LatCoord
import org.fayalite.sjs.comm.XHR
import org.fayalite.sjs.input.InputBootstrap

object MetaBootstrap {
  def init(): Unit = {
    XHR.requestTopLevelFiles{
      f =>
        println("Attempting mkWord", f.head)
        InputBootstrap.mkWord(f.head, LatCoord(27*5, 27*5))
    }

  }

}
