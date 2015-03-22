package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.canvas.Schema.{Act, Position, Elem}
import Canvas._

object ButtonFactory {

  def apply(
             text: String,
             x: Int,
             y: Int,
             trigger: Act,
              flag: Enumeration#Value
             ) : () => Elem = {
    apply(Elem(text, Position(
      x, y), trigger, flag=flag))
  }

  def apply(
             elem: Elem
             ) : () => Elem = {
    // add font and color implicits here on returned anon func.
    val draw = () => {
      ctx.font = s"15pt Calibri"
      ctx.fillStyle = "white"
      ctx.fillText(elem.name, elem.position.x, elem.position.y)
      val metrics = ctx.measureText(elem.name)
      val properElem = elem.copy(
        position = elem.position.copy(
          dx = metrics.width, dy = 15)
      )
      properElem.register()
      properElem
    }
    draw
  }
}

