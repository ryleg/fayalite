
package org.fayalite.sjs

import org.fayalite.sjs.canvas.CanvasBootstrap
import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.raw.HTMLCanvasElement

/**
  * Created by aa on 3/17/2016.
  */
object Schema extends SJSHelp {

 // {
    import upickle._
    // json.read // json.write
 // }

  case class ParseRequest (
                            code: String,
                            cookies: String,
                            requestId: String
                          )
  /**
    * This is used because the canvas
    * engine requires setting flags in advance of draw
    * calls, these are the typical modified GUI
    * declarations required most commonly, feel
    * free to add on additional specifications
    * @param font: A string as expected in CSS
    * @param fillStyle : Hex prefixed color code
    * @param globalAlpha : Zero to one float value
    *                    as in png for draw call
    */
  case class CanvasStyling(
                            font: String = "14pt monospace",
                            fillStyle: String =  lightBlue,
                            globalAlpha: Double = 1D
                          )

  case class CanvasContextInfo(
                              canvas: HTMLCanvasElement,
                              context: CanvasRenderingContext2D,
                              tileSize: Int = CanvasBootstrap.minSize
                              ) {
    var location = LatCoord(0, 0)
  }

  case class LatCoord(x: Int, y: Int)(implicit squareTileSize : Int =
  CanvasBootstrap.minSize) {
    def *(o: Int) = {
      this.copy(x*o, y*o)
    }
    def up0 = this.copy(y=0)
    def left0 = this.copy(x=0)
    def right = this.copy(x=x+1)
    def right(n: Int) = this.copy(x=x+n)
    def left = this.copy(x=x-1)
    def up = this.copy(y=y-1)
    def down = this.copy(y=y+1)
    def *(o: LatCoordD) = { // elementwise
      o.copy(x*o.x, y*o.y)
    }
    def +(o: LatCoord) = this.copy(o.x+x, o.y+y)
    def str = s"x:$x,y:$y"
    def toAbsolute = {
      LatCoord(x*squareTileSize, y*squareTileSize)
    }
    def fromAbsolute = {
      LatCoord(x/squareTileSize, y/squareTileSize)
    }

  }

  case class LatCoordD(x: Double, y: Double) {
    def +(other: LatCoordD) = {
      this.copy(other.x+x, other.y+y)
    }
    def +(otheri: Int) = {
      val other = LatCoord(otheri, otheri)
      this.copy(other.x+x, other.y+y)
    }
    def -(o: LatCoordD) = {
      this.copy(x-o.x, y-o.y)
    }
    def -(o: LatCoord) = this.copy(x-o.x, y-o.y)
    def -(oi: Int) = {
      this.copy(x-oi, y-oi)
    }
/*    def fillRect(dxDy: LatCoordD) = {
      LatCoord2D(this, dxDy).fillRect()
    }*/
    def str = s"x:$x,y:$y"
  }


}

