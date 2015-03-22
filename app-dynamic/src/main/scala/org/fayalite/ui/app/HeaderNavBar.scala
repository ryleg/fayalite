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

  case class Tab(tab: Elem, subTabs: List[Elem])

  val reloadF : Act = { () =>
    import PersistentWebSocket._
    pws.ws.close()
    DisposableWebSocket.reload()
    ()
  }

  type ButtonSpec = (String, Act)
  type NavSpec = (ButtonSpec, List[ButtonSpec])

  val reload : NavSpec = "ReloadJS" -> reloadF -> List[(String, Act)](
    "Blank" -> Act0
  )

  val account : NavSpec = "Account" -> Act0 -> List[(String, Act)](
    "OAuth" -> OAuth.redirect
  )

  val buttons : List[NavSpec] = List(
    reload,
    account
  )

  val spacingX = 112
  val xOffset = 42
  val yOffset = 42
  var curX = xOffset
  var curY = yOffset
  val subTabSpacing = 42

  object ButtonType extends Enumeration {
    type ButtonType = Value
    val Tab, SubTab = Value
  }

  def clearSubTabs() : Unit = {
    Canvas.elementTriggers.filter{
      _._1.flag == ButtonType.SubTab}.foreach{
      _._1.deregister()
    }
  }

  def setupButtons() = {
    buttons.zipWithIndex.foreach{
      case (((tabName, trigger), subTabs), bIdx) =>
        val curX = xOffset + spacingX*bIdx
        subTabs.zipWithIndex.map{
          case ((stName, stTrigger), stIdx) =>
            val curSubX = 25
            val curSubY = 120 + stIdx*subTabSpacing
            ButtonFactory(
              stName, curSubX, curSubY, stTrigger,
              flag=ButtonType.SubTab)
       }
      val exclusionTrigger = () => {
          clearSubTabs()
          trigger()
      }
      val buttonF = ButtonFactory(
        tabName, curX, curY, exclusionTrigger, flag=
      ButtonType.Tab
      )
      val button = buttonF()
    }
  }
}
