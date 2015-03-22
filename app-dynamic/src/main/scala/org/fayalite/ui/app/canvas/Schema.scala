package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.PersistentWebSocket
import org.fayalite.ui.app.canvas.Canvas._

object Schema {
  
  type Act = () => Unit

  case class Position(
                       x: Int,
                       y: Int,
                       dx: Double = null.asInstanceOf[Double],
                       dy: Int = null.asInstanceOf[Int]               
                       ) {
    val x2 = x + dx
    val y2 = y + dy
    def clear() = {
      Canvas.ctx.clearRect(
        x.toDouble,
        y.toDouble,
        dx,
        dy.toDouble) //rekt
    }
  }

  val Act0: Act = () => ()

  case class Elem(
                  name: String,
                  position: Position,
                  trigger: Act,
                  draw: Act = Act0,
                  tabSync: Boolean = true,
                  flag: Enumeration#Value
                   ) {
    def register() = {
      draw()
      elementTriggers = elementTriggers ++ Map(this -> {
        () => {
          if (tabSync) PersistentWebSocket.sendKV("tab", name)
          trigger()
        }
    } )
      resetCanvasTriggers()
    }
    def deregister() = {
      position.clear()
      elementTriggers = elementTriggers.-(this)
      resetCanvasTriggers()
    }


  }

}
