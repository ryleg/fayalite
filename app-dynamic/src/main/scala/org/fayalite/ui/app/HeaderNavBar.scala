package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.{ButtonFactory, Canvas}
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema.{Act, Elem}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Random, Failure, Success, Try}

import org.scalajs.jquery.jQuery

import org.fayalite.ui.app.canvas.Schema._

object HeaderNavBar {

  val spacingX = 112
  val xOffset = 42
  val yOffset = 42
  var curX = xOffset
  var curY = yOffset
  val subTabSpacing = 42


  def setupButtons() = {
    buttons.foreach{
      case ((tabName, trigger), subTabs) =>
      val buttonF = ButtonFactory(tabName, curX, curY, trigger)
      curX += spacingX
    }

  }


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

  case class Tab(tab: Elem, subTabs: List[Elem])


  val buttons = List(
    "ReloadJS" -> Act0 -> {
      () => {
        import PersistentWebSocket._
      pws.ws.close()
      DisposableWebSocket.reload()
        ()
    }: Unit},
   /* "Servers" -> { () => {
      List(
      "Launch" ->
        ()
      )
    },*/
    "Account" -> Act0 -> { () =>
      "OAuth" -> {
         () =>
           window.location.href = OAuth.getURL()
           ()
      } : Unit
    }
  )

}
