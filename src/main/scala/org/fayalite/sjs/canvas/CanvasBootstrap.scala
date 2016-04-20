package org.fayalite.sjs.canvas

import org.scalajs.dom._


/**
  * For accessing quick DOM info relevant to
  * canvas parameters / positioning /
  * client info
  */
trait DOMHelp {

  /**
    * Get the viewscreen for the client with measurements for
    * determining screen height to determine canvas tiling
    * requirements
    * @return : Rect for inspection of client viewscreen parameters
    */
  def getRect = document.body.getBoundingClientRect()

  /**
    * Adjusted width with a clip to prevent scroll bar appearing
    * Hack for getting to display properly in desktop browser,
    * untested elsewhere
    * @return : Clip adjusted pixel width of viewport
    */
  def w = document.documentElement.clientWidth - 18 // wtf? it makes a scroll bar without this offset

  /**
    * Adjusted height with clip margin for preventing scrollbars
    * WARNING: Complete hacks here
    * @return : Adjusted pixel height
    */
  def h = document.documentElement.clientHeight - 50

}

/**
  * Initialize / cleanup / manage canvas references
  * across compilation / execution rounds (For now, assume
  * a single compilation round to prevent errors / ghost nodes)
  */
object CanvasBootstrap extends CanvasHelp
with DOMHelp
{

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

    val numDiv = 9

    val tileXWidth = w / numDiv
    val tileYHeight = h / numDiv

    val canvasBuilder = (zIndex: Int, tileXIndex: Int, tileYIndex: Int) => {
      val cvTx = createCanvas(zIndex)
      appendBody(cvTx.canvas)
      cvTx.canvas.width = tileXWidth
      cvTx.canvas.height = tileYHeight
      cvTx.canvas.style.left = (tileXIndex * tileXWidth).toString
      cvTx.canvas.style.top = (tileYIndex * tileYHeight).toString
      cvTx
    }

    val skew = 3

    for (
      x <- 0 until numDiv*skew;
      y <- 0 until numDiv*skew
    ) {
      val cv = canvasBuilder(2, x, y)
      cv.context.fillText("c: " + x + " " + y, 10, 10)
    }

  }


}
