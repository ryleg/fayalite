package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Canvas._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Failure, Success, Try}


object HeaderNavBar {

  val spacingX = 112
  val xOffset = 42
  val yOffset = 42

  var curX = xOffset
  var curY = yOffset

  def add(text: String, func: () => Unit) = {
    addButton(Elem(text, curX, curY), func)
    curX += spacingX
  }

  import PersistentWebSocket._

  val subTabs = Map(
    "Servers" ->
      ("Launch", () =>
      {
        println("Launch")
      })
  )

  val subTabSpacing = 42

  def addTab(text: String, func: () => Unit) = {
    add(text, () => {
      sendKV("tab", text)
      val curSubX = 20
      var curSubY = 120
      subTabs.get(text).foreach{
        case (stName, stTrigger) =>
          addButton(Elem(stName, curSubX, curSubY), stTrigger)
          curSubY += subTabSpacing
      }
      func()
    })
  }

  class SubTab {

  }

  class SubTabManager {
    var subTabs = List[SubTab]()
    def clearTabs() = {

    }

  }

  def setupButtons() = {
    add("ReloadJS", {() =>
      println("ReloadJS3")
      pws.ws.close()
      DisposableWebSocket.reload()
    })
    addTab("Servers", () => {
      println("Servers")
    })
  }

}