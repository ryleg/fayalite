package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema._

object ElementFactory {

  @deprecated
  def apply(
             text: String,
             x: Int,
             y: Int,
             trigger: Act,
             flag: Enumeration#Value,
             key: String = "tab",
             setActiveOnTrigger: Boolean = false,
           tabSync: Boolean = true
             ) : () => Elem = {
    apply(Elem(text, Position(
      x, y), trigger, flag=flag, key=key,
      setActiveOnTrigger=setActiveOnTrigger, tabSync = tabSync))
  }

  @deprecated
  def getDrawText(text: String, maxWidth: Option[Double] = None, font: String =
  s"15pt Calibri", fillStyle: String = "white") = {
    val draw = (x: Int, y: Int) => {
      val innerDraw: Act = () => {
        ctx.font = font
        ctx.fillStyle = fillStyle
        maxWidth.foreach{mw => ctx.fillText(text, x, y, maxWidth=mw)}
        if (maxWidth.isEmpty) ctx.fillText(text, x, y)
        }
      val metrics = {
        ctx.font = font
        ctx.fillStyle = fillStyle
        ctx.measureText(text)
      }
      val posWidth = maxWidth.getOrElse(metrics.width)
      (innerDraw, Position(x, y - 17, posWidth, 22))
    }
    draw
  }

  @deprecated
  def apply(
             elem: Elem
             ) : () => Elem = {
    // add font and color implicits here on returned anon func.
    val draw = () => {
      val innerDraw : Act = () => {
        ctx.font = s"15pt Calibri"
        ctx.fillStyle = "white"
        ctx.fillText(elem.name, elem.position.x, elem.position.y)
      }
      val metrics = {
        ctx.font = s"15pt Calibri"
        ctx.fillStyle = "white"
        ctx.measureText(elem.name)
      }
      //println( "metrics of " + elem.name + " width " + metrics.width)
      val newPos = elem.position.copy(
        y = elem.position.y - 17, //omg no, need buffers
        dx = metrics.width, dy = 22) // cuz textMetrics is a liar
      val properElem = elem.copy(
        position = newPos,
      draw=innerDraw
      )
      properElem.register()
      properElem
    }
    draw
  }
}
