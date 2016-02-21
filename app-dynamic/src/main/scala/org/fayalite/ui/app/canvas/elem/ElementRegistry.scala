package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.PositionHelpers.LatCoord


/**
  * Convenient way to store element representations
  * for mapping elements to coordinates and back.
  */
trait ElementRegistry {

  import scala.collection.mutable.{Map => MMap}

  val registry = MMap[LatCoord, Array[GridElement]]()

  def register(e: GridElement) = {
     registry(e.latCoord()) :+= e
  }

  def deregister(e: GridElement) = registry.remove(e.latCoord())

}
