
package org.fayalite.sjs

import org.fayalite.sjs.canvas.CanvasBootstrap
import org.scalajs.dom
import org.scalajs.dom.Node
import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.raw.HTMLCanvasElement
import rx.core.{Rx, Var}

/**
  * Created by aa on 3/17/2016.
  */
object Schema extends SJSHelp {

 // {
    import upickle._
    // json.read // json.write
 // }

  case class A(b: String)

  case class ParseRequest (
                            code: String,
                            cookies: String,
                            requestId: String
                          )

  val defaultFont = "12pt monospace"

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
                            font: String = defaultFont,
                            fillStyle: String =  lightBlue,
                            globalAlpha: Double = 1D
                          )

  trait DeleteAble {
    val node: Node
    def delete(): Unit = {
      node.parentNode.removeChild(node)
    }
  }

  case class CanvasContextInfo(
                              canvas: HTMLCanvasElement,
                              context: CanvasRenderingContext2D,
                              tileSize: Int = CanvasBootstrap.minSize,
                              text: Option[String] = None
                              ) extends DeleteAble {
    val node = canvas
    var location = LatCoord(0, 0)
    var isMoving = false
  }

  case class LatCoord(x: Int, y: Int)(implicit squareTileSize : Int =
  CanvasBootstrap.minSize) {
    def *(o: Int) = {
      this.copy(x*o, y*o)
    }
    def up0 = this.copy(y=0)
    def left0 = this.copy(x=0)
    def right = this.copy(x=x+1*squareTileSize)
    def right(n: Int) = this.copy(x=x+n*squareTileSize)
    def left = this.copy(x=x-1)
    def up = this.copy(y=y-1*squareTileSize)
    def down = this.copy(y=y+1*squareTileSize)
    def down(n: Int) = this.copy(y=y+n*squareTileSize)
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

/*
  case class ChiralCell(
                         side: Either[LatCoord, LatCoord]
                       ) {
    val offset = side match {
      case Left(lc) => lc
      case Right(lc) => lc.copy(x=lc.x+1)
    }
  }
*/


  implicit def d2tolc(d2: (Double, Double)) : LatCoordD = {
    LatCoordD(d2._1, d2._2)
  }

  implicit def i2tolc(d2: (Int, Int)) : LatCoordD = {
    LatCoordD(d2._1, d2._2)
  }

  implicit def i2t2olc(d2: (Int, Int)) : LatCoord = {
    LatCoord(d2._1, d2._2)
  }

  /*
  // This kills rx.ops._ import carefully. Or make a nested class.
    implicit class RxOps[T](rxx: Rx[T]) {
      def reset(f: => T) = {
        rxx.parents.map{q => Try{q.asInstanceOf[Var[T]]() = f}}
      }
    }
  */

  def xy(x: Double = 0D, y: Double = 0D): LatCoordD = LatCoordD(x,y)
  def xyi(x: Int = 0, y: Int = 0): LatCoord = LatCoord(x,y)
  def vl(x: Int = 0, y: Int = 0) = Var(xyi(x,y))
  //def vl(x: Int = 0) = Var(xyi(x,x))

  implicit class RxOpsExt[T](t: T) {
    def v = Var(t)
    def rx = Rx{t}
  }

  def lc0 = LatCoord(0, 0)
  def lcd0 = LatCoordD(0D, 0D)
  def l0 = Var{LatCoord(0, 0)}
  def ld0 = Var{LatCoordD(0D, 0D)}
  def l20 = Var{LatCoord2(LatCoord(0, 0),LatCoord(0, 0))}
  def ld20 = Var{LatCoord2D(lcd0, lcd0)}

  type LC = LatCoord
  type LCD = LatCoordD
  type LC2 = LatCoord2
  type LC2D = LatCoord2D
  type VL = Var[LatCoord]
  type VLD = Var[LatCoordD]
  type VL2 = Var[LatCoord2]
  type VL2D = Var[LatCoord2D]

  case class LatCoord2(xy: LatCoord, xy2: LatCoord) {
    def str = xy.str + "|" + xy2.str
  }

  case class LatCoord2D(xy: LatCoordD, xy2: LatCoordD) {
    def str = xy.str + "|" + xy2.str
    def x = xy.x
    def y = xy.y
    def dx = xy2.x
    def dy = xy2.y
    def plus1(other: LCD) = {
      this.copy(xy = other.+(this.xy))
    }
  }

  case class XYI(x: Var[Int], y: Var[Int]) {
    def plus(other: XYI) = {
      this.copy(
        x=Var(x() + other.x()), y=Var(y() + other.y())
      )
    }
  }

}

