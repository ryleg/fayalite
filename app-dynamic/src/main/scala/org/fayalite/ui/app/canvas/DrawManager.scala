package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.canvas.Schema.Elem
import org.scalajs.dom.UIEvent


object DrawManager {

  var currentElemManagers = List[ElemManager]()

  def onresize(uie: UIEvent) = {
    redraw()
  }

  def redraw() = {

  }

}

class ElemManager(val e: Elem,
                 val draw: () => Unit
                   )
{

}


class DrawManager {



}
