package org.fayalite.sjs

import scala.scalajs.js
import scala.scalajs.js._

/**
 * Handler for contract between static / dynamic scala.js classloaders.
 * Incomplete.
 */
object Dynamo {

  def eval(pm: Dynamic) = {
    js.eval(pm.code.toString)
    val curBridge = "dynamic-bridge"
    val retVal = js.eval(s"org.fayalite.ui.app.DynamicEntryApp().fromBridge('$curBridge');")
  }
}
