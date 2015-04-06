package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.canvas.Schema.Position
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSStringOps

object Text {

  implicit class Measure(text: String) {
    def measure = ctx.measureText(text)
    def width = measure.width
    def canvasIdx = text.zipWithIndex.foldLeft(List[CharTextIdx]()) {
      case (acc, (nv, nvidx)) =>
      val curs = acc.lastOption.map{_.text.+(nv)}.getOrElse(nv.toString)
      acc :+ CharTextIdx(curs, nvidx, curs.measure.width)
    }
    def charTextIdx = text.zipWithIndex.foldLeft(List[CharTextIdx]()) {
      case (acc, (nv, nvidx)) =>
        val curs = acc.lastOption.map{_.text.+(nv)}.getOrElse(nv.toString)
        acc :+ CharTextIdx(curs, nvidx, curs.measure.width)
    }
  }

  val interLineSpacing = Var(20)

  def apply(text: String, x: Int, y: Int) = new Text(Var(text),Var(x), Var(y))

  implicit def asNode(text: Text) : Node = new Node(Var(text))

  case class CharTextIdx(
                        text: String,
                        charIdx: Int,
                        width: Double
                          )

}

/**
 * Reactive text handler for any canvas text rendering.
 * @param text : String to display, should be short, editable boxes are implemented elsewhere
 * @param widthCutoff : Length to cut off by, if unspecified text will render until it hits the edge of the canvas.
 * @param font : String of font formatting -- to be updated properly to formatted strings off of Int size.
 * @param fillStyle : JS fill style
 * @param x : X offset
 * @param y: Y offset
 */
class Text(
            val text: Var[String], // change to styled text to absorb styling monad ops
            val x : Var[Int],
            val y: Var[Int],
            val widthCutoff: Var[Option[Double]] = Var(None),
            val font: Var[String] =Var(s"14pt Calibri"),
            val fillStyle: Var[String] = Var("white")
            ) {




  import Text._
  // TODO : Add reactive observers to redraw text automatically on changes.
  // and add listeners to handle inter-element interactions through an observation lattice cache.
  // i.e. onclick should be subdivided where elements index according to what quadrant of the screen they're in
  // to avoid checking every element. Can use any number of subdivisions moreso than quadrants.

  println("new text " + s"${text()} ${x()} ${y()}")
  val charPosition = Rx {
    //println("char Pos")

    //println("char pos " + cp)
    //cp
  }

  val splitText = Rx {
    import js._
    import JSStringOps._
    val st = text().split("\n")
 //   st.zipWithIndex.foreach{println}
   //      println("splitTextlen" + st.toList)
    st
  }

  /**
   * Canvas context requires setting flags before any operation, hence this wrapper.
   * @param f: Render func
   * @tparam T: Render return type
   * @return : Result of render func
   */
  def style[T](f : => T): T = {
    val prevFont = ctx.font
    val prevFillStyle = ctx.fillStyle
    ctx.font = font()
    ctx.fillStyle = fillStyle()
    val ret = f
    ctx.font = prevFont
    ctx.fillStyle = prevFillStyle
    ret
  }

  val width = Rx { style { ctx.measureText(text()).width}}

  val widths = Rx {
    style { splitText().map{t => ctx.measureText(t).width} }
  }

  val maxWidthActual = Rx { widthCutoff().getOrElse(width())}
  //widths().max}

  /**
   * Return the operation that will render the text. Does not handle clearing positions in advance
   * assumes a blank area.
   * @param tex : String to draw
   * @param xi : X offset
   * @param yi : Y offset
   * @param mxWidth : Maximum length of text to draw
   */
  def drawActual(tex: String, xi: Int, yi: Int, mxWidth: Option[Double]) = {
    //println("draw drawActual " + tex + s" $xi $yi")

    style { mxWidth.foreach { mw => ctx.fillText(tex, xi, yi, maxWidth = mw)}
      if (mxWidth.isEmpty) ctx.fillText(tex, xi, yi) }
  }

  val draw = Rx { () => {
    style {
      if (splitText().length == 1) {
        drawActual(text(), x(), y(), widthCutoff())
      } else {
        splitText().toList.zipWithIndex.foreach { case (tex, idx) =>
          val ya = y() + idx*interLineSpacing()
     //     println("draw call " + tex + s" $idx ${x()} ${ya}")
          drawActual(tex, x(), ya, widthCutoff())
        }
      }
    }
  }
  }

  val position = Rx {
    val posWidth = widthCutoff().getOrElse(maxWidthActual())
    val dy = 22 + (splitText().length - 1)*interLineSpacing()
    Position(x(),
      y() - 17,  //*splitText().length
      posWidth,
      dy)
    //*splitText().length)
  }

  val onRedraw = Var(())

  /**
   * Clear the position (automatically determined by text metrics) and redraw
   * text
   */
  def redraw() = {
    position().clear()
    draw()()
    onRedraw() = ()
  }

  val widthAdjustment = Obs(widthCutoff, skipInitial = true) {
    widthCutoff().foreach{_ => redraw()}
  }

}
