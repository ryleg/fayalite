
/**
 * Created by ryle on 1/29/15.
 */
package org.fayalite.ui.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}

object DynamicEntryApp extends JSApp {

  // class Something extends MagicSerialize
  // kyro.registry(something)

  @JSExport
  def fromBridge(bridge: String): String = {
    println("From Bridge: " + bridge)
    // setInterval(_ => println("hhey"), 1000)
    "Visited basdasfkalsdMANG"
  }

  def main(): Unit = {

//    Canvas.initCanvas()
  //  implicit val wsw = new WSWrapper(WS_URI)
    val tjson = """{"yo": 1}"""
    println(JSON.parse(tjson).yo)


  }
}

