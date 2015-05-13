package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.App
import org.fayalite.ui.app.canvas.Input.{Key, Mouse}
import org.fayalite.ui.app.canvas.elem.Drawable.CanvasStyling
import org.fayalite.ui.app.canvas.elem.PositionHelpers.{LatCoord, LatCoordD}
import org.fayalite.ui.app.canvas.{Input, Schema, Canvas}

import rx.ops._
import rx._

import scala.util.{Try, Failure, Success}

object Grid {

  def apply() = new Grid()

  // doesnt seem to work?
  implicit def intToVarInt(db: Double) : Var[Double] = Var(db)

  case class ChiralCell(
                       side: Either[LatCoord, LatCoord]
                         ) {
    val offset = side match {
      case Left(lc) => lc
      case Right(lc) => lc.copy(x=lc.x+1)
    }
  }

}
import Grid._
import PositionHelpers._


class GridTranslator(grid: Grid) {

  private val pos: Rx[LatCoord2D] = grid.pos
  private val spacing: Rx[LatCoordD] = grid.spacing

  val numColumns = Rx {
    (pos().dx() / spacing().x).toInt
  }

  val numRows = Rx {
    (pos().dy() / spacing().x).toInt
  }

  implicit def lcChiral(lc: LatCoordD): ChiralCell = {
    val lct = lc : LatCoord
    val lcti = lct : LatCoordD
    val cellMiddle = lcti.x + spacing().x/2
    ChiralCell{if (lc.x < cellMiddle) Left(lct) else Right(lct)}
  }

  implicit def latCoordTranslNR(lc: LatCoord): LatCoordD = {
    LatCoordD(lc.x*spacing().x, lc.y*spacing().y)
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslTNR(lc: LatCoordD): LatCoord = {
    def sx = spacing().x
    def sy = spacing().y
    val fx = Math.floor(lc.x/sx).toInt
    val fy = Math.floor(lc.y/sy).toInt
    LatCoord(fx, fy)
  }
  // TODO: Move to translator trait and add implicit grid param
  implicit def latCoordTransl(latCoord: Rx[LatCoord]): Rx[LatCoordD] = {
    latCoord.map{
      lc => lc : LatCoordD
    }
  } // Switch to monad transformer. Functor -> Functor Inverse
  // Def TypeFunctorCoordinateTranslatorInverse
  implicit def latCoordTranslT(latCoord: Rx[LatCoordD]): Rx[LatCoord] = {
    latCoord.map{
      lc => lc : LatCoord
    }
  }
}
import Canvas.{widthR=>width,heightR=>height}

class Grid(
            val spacing: Var[PositionHelpers.LatCoordD] =
  Var(PositionHelpers.LatCoordD(22D, 22D)),
            val elementBuffer: Var[Int] = Var(1)
       //     val maxRows: Int = 100,
      //    val maxColumns: Int = 100
            )
  extends Element with Drawable
{

 val bodyArea = Canvas.rect.map{
    cr =>
      val wa = ((Canvas.widthR()/spacing().x).toInt)*spacing().x
      val ha = ((Canvas.heightR()/spacing().y).toInt)*spacing().y
      println("cr width" + cr.width + " cr botom" + cr.bottom)
     LatCoord2D(LatCoordD(0D, 0D),
       (wa,ha) : LCD
     )}
 //bodyArea //


  override val pos = bodyArea/* ld20.map{
    _.copy(xy2=(Canvas.widthR()  // - cr.right
     , Canvas.heightR()
      //- cr.bottom
      ) : LCD
    )}*/
   val gridTranslator = new GridTranslator(this)
  import gridTranslator._
  implicit val grid_ = this

  override val styling = CanvasStyling(
fillStyle="#6897BB",
globalAlpha = .3
)
  def cols = gridTranslator.numColumns()
  def rows = gridTranslator.numRows()

  val cursorDxDy =Var(spacing.map{_.-(1)}())

  val hover = new GridRect(
    dxDy=Var(LatCoordD(spacing().x - 2, 1)),
    offset = Var(LatCoordD(1D, spacing().y-1D))
  )
  val selector = new Selector(this)

  /**
   * Find nearest line between characters by cell midpoint.
   */
  val cursorXY = Mouse.click.map{c => c: ChiralCell}

  val cursor = new GridRect(
    dxDy=Var(LatCoordD(1, spacing().y-2)),
    offset = Var(LatCoordD(1D, 1D))
  )
  Obs(cursor.latCoord){
    if (cursor.latCoord().x > numRows()+1
    ) cursor.latCoord() = cursor.latCoord().left.left
    if (cursor.latCoord().y > numColumns()-2
    ) cursor.latCoord() = cursor.latCoord().up
    if (cursor.latCoord().x < 0
    ) cursor.latCoord() = cursor.latCoord().copy(x=0)
    if (cursor.latCoord().y < 0
    ) cursor.latCoord() = cursor.latCoord().copy(y=0)
  }

    val tp = LatCoordD(10, 5)

  cursorXY.foreach{
    cc =>
      cursor.latCoord() = cc.offset
     // style{LatCoord2D(cc.offset,
    //    tp).fillRect()}(CanvasStyling(fillStyle = "red"))
  }

  /**
   * Find what cell mouse is currently on.
   */
  val cellXY = Rx {
    Mouse.move() : LC
  }

  cellXY.foreach{ cxy => hover.latCoord() = cxy.copy(x=cxy.x) }

  def clear() : Unit = {} // TODO : Change to Pos based clear
  def draw() : Unit = { // TODO : Make pretty

    for (row <- 0 until numRows()+1) {
      val lineCoord = LatCoord(0, row) // change to Inv map on next func.
      val lineArea = LatCoordD(pos().dx(), elementBuffer())
      LatCoord2D(lineCoord, lineArea).fillRect()
      //Canvas.ctxR().fillRect(0, row*spacing().x, pos().dx(), elementBuffer())
    }
    for (col <- 0 until numColumns()+1) {
      val lineCoord = LatCoord(col, 0)
      val lineArea = LatCoordD(elementBuffer(), pos().dy())
      LatCoord2D(lineCoord, lineArea).fillRect()
      //Canvas.ctxR().fillRect(col*spacing().y, 0, elementBuffer(), pos().dy())
    }
  }

  redraw()

}

class Selector(grid: Grid) {


  def select(lc: LatCoord) = new GridRect(xyi = Var(lc),
    offset=Var(LatCoordD(grid.spacing().x - 5D, 1D)),
    dxDy=Var(LatCoordD(
      3D, grid.spacing().y/2)), flashing=false, alpha=.4,
  fill = "#2973C9"//, extraFill = List(-grid.spacing().x + 3D -> 3D)
   )(grid)
  val canSelect = Var(false)
  val selTable = scala.collection.mutable.Map[LatCoord, GridRect]()

  val curSel = Var(List[LatCoord]())

  def hasSelection = curSel().nonEmpty

/*  def genSel(
            start: LatCoord, end: LatCoord
              ) = {
    select(c)
  }*/
val startSel = Var(LatCoord(0,0))

  Schema.TryPrintOpt {
    Obs(Mouse.move) {
            import grid.gridTranslator._
          val c = Mouse.move() : LatCoord
        Try {
          if (canSelect()) {
            val s = startSel()
            val dy = c.y - s.y
            val dx = c.x - s.x
            val dir = c.y.compare(s.y)
            val xdr = c.x.compare(s.x)
            val mul = dy > 1
            val sing = dy == 1

            if (dx > 0 && dy > 0) grid.cursor.latCoord() = c

            val srt = Seq(c.y, s.y).sorted
            val srx = Seq(c.x, s.x).sorted
            val srxl = srx match {
              case Seq(u, a) =>
                (u to a).toList
            }
         //   println("srxl " + srxl)
            val xall = if (dy != 0) List() else srxl.map{zz => (zz -> c.y) : LatCoord}
       //     println(srt + "srt")

            val mid = if (dy == 0 ) List() else {
         val mida = Seq(c.y, s.y).sorted match {
           case Seq(u, a) =>
             (u to a).toList.tail.dropRight(1)
         }
     //   println(mida)
         val othery = {if (dir > 0) 1 else 0} + s.y + dir*(mida.size+1)

         val topifd = {if (dir > 0) {
                  (s.x until grid.rows)
                } else (0 until s.x)}.map{_ -> s.y: LatCoord}.toList

         val topifd2 = {if (dir < 0) {
           (c.x until grid.rows)
         } else (0 until c.x)}.map{_ -> (if c.y) : LatCoord}.toList


println(topifd2 + "topifd2")
         mida.flatMap { m => (0 until grid.cols).map { xxm =>
           xxm -> m: LatCoord
         }} ++ topifd ++ topifd2
       }
         /*   println("s " + s)
            println("c " + c)*/
            //table is row column.
            val midc = if (dy == 0) List() else mid
            val all = xall ++ midc
/*
            println("all " + all)
            println("xall " + xall)
            println("midc " + midc)
*/
            selTable.foreach{q => if (!all.contains(q._1)) q._2.visible() = false}

            (all).map{lxm =>
        //      println("turn on " + lxm)
              val itr = selTable.getOrElseUpdate(lxm, select(lxm))
              itr.visible() = true
            }
            curSel() = all


          }
        }
    }


    var selObs = Obs(Input.Mouse.up) {

    }

    import Input.Mouse._
    Obs(Input.Mouse.up, skipInitial = true) {
        Try {
          println("mouseup")
          canSelect() = false
        }
    }
    Obs(down, skipInitial = true ) {
      Try {
        println("mousedown")
        startSel() = grid.hover.latCoord()
        canSelect() = true
        selTable.foreach{_._2.visible() = false}}
      }
    }


}