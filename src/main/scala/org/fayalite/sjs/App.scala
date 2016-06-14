package org.fayalite.sjs

//import rx.ops.{DomScheduler, Timer}

//import scala.scalajs.js.Dynamic.{global => g}
import rx.ops.{DomScheduler, Timer}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * Static entrypoint to displayable app. Used here for
  * testing until we can get a workflow like Haoyi Li's
  * workbench (autoincremental recompiles and code updates
  * to sync to active page window) working properly.
  *
  * Assumes page refresh required to reset between compiles
  * for now
  */
object App extends JSApp {

  @JSExport
  def main(): Unit = {
    println("SJS fayalite initialized")
    canvas.CanvasBootstrap.init()
    input.InputBootstrap.init()
    meta.MetaBootstrap.init()
  }

}

