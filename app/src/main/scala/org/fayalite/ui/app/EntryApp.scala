
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.fayalite.ui.app.io.WSWrapper
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp
import org.fayalite.ui.app.Canvas

object EntryApp extends JSApp {

  // TODO : parse from somewhere
  val WS_URI = "ws://localhost:8080/"

  def main(): Unit = {

    Canvas.initCanvas()
    implicit val wsw = new WSWrapper(WS_URI)

  }
}

