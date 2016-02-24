package org.fayalite.sjs

import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.Future
import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Success, Try}

object App extends JSApp {

  val filePrefix = "file:///"
  val compileOutput = "/target/scala-2.10/fayalite-fastopt.js"
  val WS_URI = "ws://localhost:8080/"

  @JSExport
  def main(): Unit = {
    println("yo")
  }
}

