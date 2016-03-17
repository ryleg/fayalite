package org.fayalite.sjs

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object App extends JSApp {

  val filePrefix = "file:///"
  val compileOutput = "/target/scala-2.10/fayalite-fastopt.js"
  val WS_URI = "ws://localhost:8080/"

  @JSExport
  def main(): Unit = {
    println("yo")
  }
}

