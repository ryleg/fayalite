package org.fayalite.sjs

//import rx.ops.{DomScheduler, Timer}

//import scala.scalajs.js.Dynamic.{global => g}
import rx.ops.{DomScheduler, Timer}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object App extends JSApp {

  @JSExport
  def main(): Unit = {
    println("SJS fayalite initialized")
    canvas.CanvasBootstrap.init()
    input.InputBootstrap.init()
    meta.MetaBootstrap.init()
  }

}

