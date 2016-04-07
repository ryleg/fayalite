package org.fayalite.ui.app.canvas.elem


import org.fayalite.ui.app.canvas.PositionHelpers
import org.fayalite.ui.app.canvas.elem.Grid.ChiralCell
import rx._
import rx.ops._

import PositionHelpers._

class GridTranslator(grid: Grid) {

  private val pos: Rx[LatCoord2D] = grid.pos
  private val spacing: Rx[LatCoordD] = grid.spacing

  val numColumns = Rx {
    (pos().dx / spacing().x).toInt
  }

  val numRows = Rx {
    (pos().dy / spacing().x).toInt
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
