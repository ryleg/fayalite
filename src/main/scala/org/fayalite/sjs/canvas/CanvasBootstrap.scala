package org.fayalite.sjs.canvas

import org.scalajs.dom._

/**
  * Initialize / cleanup / manage canvas references
  * across compilation / execution rounds (For now, assume
  * a single compilation round to prevent errors / ghost nodes)
  */
object CanvasBootstrap extends CanvasHelp
with DOMHelp
{

  val tileXWidth = 300 //w / numDiv
  val tileYHeight = 300// h / numDiv

  def getTileOn(x: Double, y: Double) = {
    val xIdx = (x/tileXWidth).toInt
    val yIdx = (y/tileYHeight).toInt
    xIdx -> yIdx
  }

  /**
    * Start by populating the DOM dynamically with tiled canvases
    * and bg / offscreen ones for later usage. Allocates a fair
    * number of objects so might require tuning if client performance
    * is an issue
    */
  def init() : Unit = {

    println("Adjusted client width " + w)
    println("Adjusted client height " + h)
    println("Initializing canvas tiles")

    document.body.style.overflow = "hidden"

    val tm = buildTileMatrix()



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
    val numDiv = 9

    val canvasBuilder = (zIndex: Int, tileXIndex: Int, tileYIndex: Int) => {
      val cvTx = createCanvas(zIndex)
      appendBody(cvTx.canvas)
      cvTx.canvas.width = tileXWidth
      cvTx.canvas.height = tileYHeight
      cvTx.canvas.style.left = (tileXIndex * tileXWidth).toString
      cvTx.canvas.style.top = (tileYIndex * tileYHeight).toString
      cvTx.setBackground(ansiDarkGrey)
      cvTx.setBorder(lightBlue, 1)
      cvTx.grid(5)
    }

    val skew = 1

    val tileCount: Int = 5 //numDiv * skew

    val cvs = for (
      x <- 0 until tileCount;
      y <- 0 until tileCount
    ) yield {
      val cv = canvasBuilder(2, x, y)
      //cv.context.fillText("c: " + x + " " + y, 10, 10) // Debug turn on for
      // visualizing tile layout
      cv
    }

    val tileMatrix = cvs.grouped(tileCount).map {
      _.toArray
    }.toArray

    tileMatrix
  }
}
