package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.state.StateSync
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.Future
import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Success, Try}

object DynamicEntryApp extends JSApp {

  // Used??
  val fp = "file:///Users/ryle/Documents/fayalite/app-dynamic/target/scala-2.10/fayalite-app-dynamic-fastopt.js"
  val WS_URI = "ws://localhost:8080/"

  @JSExport
  def main(): Unit = {
    StateSync.processBridge("asdf")
  }
}

//    if (oAuthCatch()) bridge // return bridge without initializing
// anything, allow the callback page to handle this.
