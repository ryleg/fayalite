
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Canvas
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

  val fp = "file:///Users/ryle/Documents/fayalite/app-dynamic/target/scala-2.10/fayalite-app-dynamic-fastopt.js"
  val WS_URI = "ws://localhost:8080/"

  @JSExport
  def fromBridge(bridge: String): String = {
    val attempt = Try {
      if (window.location.href.contains("access")) {
        println("reloading due to oauth catch url")
        window.location.href = "http://localhost:8080"
        bridge
      } else {
        Canvas.initCanvas()
        import PersistentWebSocket._
        sendV("Init")
        HeaderNavBar.setupButtons()
        Editor.apply()
        StateSync.processBridge(bridge)
      }
    }
    attempt match {
      case Success(x) => //println("bridge success");
       x
      case Failure(e) => e.printStackTrace(); "Failure"
    }
  }

  def main(): Unit = {

//    Canvas.initCanvas()
  //  implicit val wsw = new WSWrapper(WS_URI)
   // val tjson = """{"yo": 1}"""
    //println(JSON.parse(tjson).yo)
    println("Main")


  }
}

