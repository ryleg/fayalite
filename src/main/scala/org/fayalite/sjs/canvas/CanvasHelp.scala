
package org.fayalite.sjs.canvas

import org.fayalite.sjs.SJSHelp
import org.fayalite.sjs.Schema.{CanvasContextInfo, CanvasStyling, LatCoord}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{CanvasRenderingContext2D, HTMLCanvasElement}


/**
  * Created by aa on 3/17/2016.
  */
trait CanvasHelp extends SJSHelp {

  /**
    * Pretty easy style for text draw, matches
    * standard black bg. Similar to IJ Darcula
    */
  val defaultStyling = CanvasStyling()

  /**
    * Canvas context requires setting flags before any operation, hence this wrapper.
    *
    * @param f: Render func
    * @tparam T: Render return type
    * @return : Result of render func
    */
  def style[T](f : => T)(implicit ctx: CanvasRenderingContext2D,
                         stylingEv: CanvasStyling = CanvasStyling())
  //           )
  : T = {
    val prevFont = ctx.font
    val prevFillStyle = ctx.fillStyle
    val prevAlpha = ctx.globalAlpha
    ctx.font = stylingEv.font
    ctx.fillStyle = stylingEv.fillStyle
    ctx.globalAlpha = stylingEv.globalAlpha
    val ret = f
    ctx.font = prevFont
    ctx.fillStyle = prevFillStyle
    ctx.globalAlpha = prevAlpha
    ret
  }

  def color[T](f : => T)(implicit ctx: CanvasRenderingContext2D,
                         fillStyle: String,
                         alpha: Double = 1D)
  : T = {
    style(f)(ctx, CanvasStyling(fillStyle = fillStyle, globalAlpha = alpha))
  }

  /**
    * Canvas optimizations require many small patch work
    * like layers of canvas, and storing elements off-screen
    * for buffering in memory, etc. you should expect
    * to deal with a large num of canvas elements on the order of 5-20
    *
    * @return : Elements matching tag canvas
    */
  def getAllCanvasTaggedElements = {
    document.body.getElementsByTagName("canvas")
  }

  /**
    * Any canvas appearing on the dom is either a ghost
    * from a previous operation and hence requires cleaning
    * or an error in providing a non-generated html page that
    * has pre-existing canvas tags. ONLY generate canvas through
    * this interface ideally unless you know what you're doing
    *
    * @return : Whether or not the body has any canvas tags left
    *         over from some previous op.
    */
  def domCanvasNodesUninitialized = {
    getAllCanvasTaggedElements.length == 0
  }

  /**
    * Quick helpers for grabbing context for instance
    * and / or canvas manipulations that are not
    * native like already.
    *
    * @param hte : The scala representation of the DOM Node
    *            holding the canvas
    */
  implicit class HTMLCanvasElementHelp(hte: HTMLCanvasElement) {
    /**
      * Every canvas manipulation that does heavy lifting requires
      * context object similar to a SparkContext for handling
      * interaction requests. For regular manipulations that are dom
      * related use the original Canvas Element object, like for
      * setting clicks or alerts or something. For draw manipulation
      * you must use the context which forces some strange
      * OpenGL like declarations (i.e. you need to set styling before a
      * call and it must respect thread locks properly to prevent
      * two draw calls using different styles accidently.)
      *
      * @return Object for manipulating canvas pixel information.
      */
    def ctx = hte
      .getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]
  }

  implicit class ContextExtensions(ctx: CanvasContextInfo) {

    implicit val ctxi = ctx.context

    def fill(x: Double, y: Double, dx: Double, dy: Double, hexColor: String,
             alpha: Double = 1D) = {
      color{
        ctx.context.fillRect(x,y,dx,dy)
      }(ctxi, hexColor, alpha)
    }

    /**
      * Color the entire canvas with a single
      * pixel color type
      *
      * @param hexColor : HTML color code as in SJSHelp
      */
    def setBackground(hexColor: String, alpha: Double = 1D) = {
      fill(
        0D,
        0D,
        getWidth,
        getHeight,
        hexColor,
        alpha
      )
    }

    val canv = ctx.canvas

    def onOff() : Unit = {
      if (isOff()) {
        turnOn()
      } else turnOff()
    }

    def changeZ(z: Int): Unit = cStyle.zIndex = z.toString
    def dropZ() = changeZ(-1)
    def upZ() = changeZ(5)
    def zIndex = cStyle.zIndex.toInt
    def turnOn(): Unit = upZ //cStyle.visibility = "visible"
    def turnOff(): Unit = dropZ //cStyle.visibility = "hidden"
    def isOff() = zIndex == -1 // cStyle.visibility == "hidden"

    def getHeight: Double = {
      ctx.canvas.height.toDouble
    }

    def getWidth: Double = {
      ctx.canvas.width.toDouble
    }

    def hLine(y: Double) = {
      fill(0D, y, getWidth, 1, lightBlue, 0.17D) // top
    }

    def vLine(x: Double) = {
      fill(x, 0D, 1, getHeight, lightBlue, 0.17D) // top
    }

    def grid(numDivs: Int) = {
      val ds = getWidth / numDivs
      for (x <- 0 until numDivs) {
        vLine(x*ds)
      }
      for (y <- 0 until numDivs) {
        hLine(y*ds)
      }
    }

    def cStyle = ctx.canvas.style

    def move(x: Int, y: Int): CanvasContextInfo = {
      cStyle.left = x.toString
      cStyle.top = y.toString
      ctx
    }
    def move(lc: LatCoord): CanvasContextInfo = {
      move(lc.x, lc.y)
    }

    def left = cStyle.left.replaceAll("px", "").toInt
    def top = cStyle.top.replaceAll("px", "").toInt

    def moveTo(other: CanvasContextInfo) = {
      move(other.left, other.top)
    }

    def setBorder(hexColor: String, numPixels: Int, alpha: Double = .17D) = {
      fill(0D, 0D, getWidth, numPixels.toDouble, hexColor, alpha) // top
      fill(0D, getHeight, getWidth, -1*numPixels.toDouble, hexColor, alpha) // bottom
      fill(0D, 0D, numPixels.toDouble, getHeight, hexColor, alpha) // left
      fill(getWidth, 0D, -1*numPixels.toDouble, getHeight, hexColor, alpha) // right
    }

  }

  /**
    * Make a simple canvas
    *
    * @param zIndex : For layering canvas
    * @return Pre-allocated context / canvas
    */
  def createCanvas(zIndex: Int = 2): CanvasContextInfo = {
    val obj = "canvas".element.asInstanceOf[dom.raw.HTMLCanvasElement]
    obj.style.position = "absolute"
    obj.style.left = "0"
    obj.style.top = "0"
    obj.style.zIndex = zIndex.toString
    CanvasContextInfo(obj, obj.ctx)
  }

  def createCanvasWithPosition(left: Int = 0,
                               top: Int = 0,
                               width: Int = 30,
                               height: Int = 30,
                               zIndex: Int = 3
                  ): CanvasContextInfo = {
    val cv = createCanvas(zIndex)
    cv.canvas.width = width
    cv.canvas.height = height
    cv.canvas.style.left = left.toString
    cv.canvas.style.top = top.toString
    appendBody(cv.canvas)
    cv
  }

  def createCanvasZeroSquare(ds: Int = 60,
                             hexColor: String = burntGold,
                             alpha: Double = 1D) = {
    val x = createCanvasWithPosition(0,0,ds,ds)
    x.setBackground(hexColor, alpha)
    x
  }


}

