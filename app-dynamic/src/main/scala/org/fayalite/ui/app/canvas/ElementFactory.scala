package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.PersistentWebSocket
import org.fayalite.ui.app.canvas.Schema.{Act, Position, Elem}
import Canvas._

import scala.scalajs.js

object ElementFactory {

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

  import rx._
  class Text(
              val   text: Var[String],
              val   maxWidth: Var[Option[Double]] = Var(None),
              val     font: Var[String] =Var(s"11pt Calibri"),
              val    fillStyle: Var[String] = Var("white"),
                val x : Var[Int],
                val y: Var[Int]
              ) {

    def style() = {
      ctx.font = font()
      ctx.fillStyle = fillStyle()
    }

    val metrics = Rx {
      style(); ctx.measureText(text())
    }

    val draw = Rx { () => {
      maxWidth().foreach { mw => ctx.fillText(text(), x(), y(), maxWidth = mw)}
      if (maxWidth().isEmpty) ctx.fillText(text(), x(), y())
    }
    }

    val position = Rx {
      val posWidth = maxWidth().getOrElse(metrics().width)
      Position(x(), y() - 17, posWidth, 22)
    }

    def redraw() = {
      position().clear()
      draw()()
    }

    val widthAdjustment = Obs(maxWidth, skipInitial = true) {
      maxWidth().foreach{_ => redraw()}
    }

  }

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

