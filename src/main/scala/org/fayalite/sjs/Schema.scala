
package org.fayalite.sjs

import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.raw.HTMLCanvasElement

/**
  * Created by aa on 3/17/2016.
  */
object Schema {

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
                            fillStyle: String =  "#A9B7C6",
                            globalAlpha: Double = 1D
                          )

  case class CanvasContextInfo(
                              canvas: HTMLCanvasElement,
                              context: CanvasRenderingContext2D
                              )

  val bgGrayBlack = "#2B2B2B"


}

