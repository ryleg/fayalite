
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Canvas
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Success, Try}

object DynamicEntryApp extends JSApp {

  val fp = "file:///Users/ryle/Documents/fayalite/app-dynamic/target/scala-2.10/fayalite-app-dynamic-fastopt.js"
  val WS_URI = "ws://localhost:8080/"

  @JSExport
  def fromBridge(bridge: String): String = {
    //println("From Bridge: " + bridge)
    val attempt = Try {
      // setInterval(_ => println("hhey"), 1000)
      // .cast[dom.HTMLCanvasElement]
      Canvas.initCanvas()
   //   println("YO N12222222")
      import Canvas._

      HeaderNavBar.setupButtons()
    }
    attempt match {
      case Success(x) => println("bridge success")
      case Failure(e) => e.printStackTrace()
    }
    "Visited bridge"
  }

  def main(): Unit = {

//    Canvas.initCanvas()
  //  implicit val wsw = new WSWrapper(WS_URI)
   // val tjson = """{"yo": 1}"""
    //println(JSON.parse(tjson).yo)
    println("Main")


  }
}

