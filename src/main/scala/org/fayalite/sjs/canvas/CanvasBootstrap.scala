package org.fayalite.sjs.canvas

import org.fayalite.sjs.input.InputBootstrap
import org.scalajs.dom._

/**
  * Accessories for canvas manipulation that
  * don't belong directly to canvas objects.
  */
trait CanvasTileUtils extends DOMHelp {

  val minSize = 27
  val bulkSize = minSize*9

  def getTileOn(x: Double, y: Double) = {
    val xIdx = (x/bulkSize).toInt
    val yIdx = (y/bulkSize).toInt
    xIdx -> yIdx
  }

  def printDebugInfo(): Unit = {
    println("Adjusted client width " + w)
    println("Adjusted client height " + h)
    println("Initializing canvas tiles")
  }

}

/**
  * Initialize / cleanup / manage canvas references
  * across compilation / execution rounds (For now, assume
  * a single compilation round to prevent errors / ghost nodes)
  */
object CanvasBootstrap extends CanvasHelp
with CanvasTileUtils
{

  /**
    * Start by populating the DOM dynamically with tiled canvases
    * and bg / offscreen ones for later usage. Allocates a fair
    * number of objects so might require tuning if client performance
    * is an issue
    */
  def init() : Unit = {

    // Initialization checks
    printDebugInfo()

    // Disable scroll bar
    document.body.style.overflow = "hidden"

    // Construct a bunch of canvas nodes each handling
    // a portion of the screen laid out in a square matrix
    val tm = buildTileMatrix()

    // Supply nodes to input processors for capture
    InputBootstrap.processTileMatrix(tm)

  }

  /**
    * Canvas is optimized to render on small tiles,
    * we choose a magic number of 9 divs and split up
    * the screen into a bunch of tiles based on initial client
    * height with a factor of 3 times as many tiles in each direction
    * as the size of the clients screen. The extra tiles are not
    * rendered and can be used as spares for moving on / offscreen
    * or for jumping up / down in z-Index and overlapping
    * other tiles
    *
    * This is pretty much intended for ignoring window resizing
    * although if you feel like redrawing for every event like that
    * be my guest -- there can always be a check later to populate
    * additional tiles on extreme events. This is a pretty basic
    * initial populator
    */
  def buildTileMatrix() = {

    val canvasBuilder = (zIndex: Int, tileXIndex: Int, tileYIndex: Int) => {
      val cvTx = createCanvas(zIndex)
      appendBody(cvTx.canvas)
      cvTx.canvas.width = bulkSize
      cvTx.canvas.height = bulkSize
      cvTx.canvas.style.left = (tileXIndex * bulkSize).toString
      cvTx.canvas.style.top = (tileYIndex * bulkSize).toString
      cvTx.setBackground(ansiDarkGrey)
      cvTx.setBorder(lightBlue, 1)
      cvTx.grid(9)
      cvTx
    }

    val skew = 1

    val tileCount: Int = 9 //numDiv * skew

    val cvs = {
      for (
        x <- 0 until tileCount ;
        y <- 0 until tileCount
      ) yield {
        val cv = canvasBuilder(2, x, y)
        //cv.context.fillText("c: " + x + " " + y, 10, 10) // Debug turn on for
        // visualizing tile layout
        cv
      }
    }

    val tileMatrix = cvs.grouped(tileCount).map {
      _.toArray
    }.toArray

    tileMatrix
  }
}
