package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.elem.ElementFactory
import org.fayalite.ui.app.canvas.{Input, Canvas}
import org.fayalite.ui.app.canvas.Canvas._
import org.fayalite.ui.app.canvas.Schema.{Act, Elem}
import org.scalajs.dom
import org.scalajs.dom._
import rx.core.Obs
import sun.net.www.content.text.plain

import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{JSON, JSApp}
import scala.util.{Random, Failure, Success, Try}


import org.fayalite.ui.app.canvas.Schema._


/**
 * Need to rewrite this to use scala.rx. Leaving it alone for now because it works
 * and it's basically just for OAuth at this point.
 */
@deprecated
object HeaderNavBar {


  val spacingX = 112
  val xOffset = 42
  val yOffset = 42
  var curX = xOffset
  var curY = yOffset
  val subTabOffset = 120
  val subTabSpacing = 42

  case class Tab(tab: Elem, subTabs: List[Elem])

  val reloadF : Act = { () =>
    import PersistentWebSocket._
    pws.ws.close()
    DisposableWebSocket.reload()
    ()
  }

  type ButtonSpec = (String, Act)
  type NavSpec = (ButtonSpec, List[ButtonSpec])

  val reload : NavSpec = "Grace" -> reloadF -> List[(String, Act)](
    "Blank" -> Act0
  )

  val account : NavSpec = "Account" -> Act0 -> List[(String, Act)](
    "OAuth" -> OAuth.redirect
  )

  /*
  Canvas.ctx.fillStyle = "red"
      Canvas.elementTriggers.foreach{
        case (e, i) =>
          Canvas.ctx.fillRect(
            e.position.x , e.position.y
            , e.position.dx, e.position.dy)
   */

//  var pasteEvent = new ClipboardEvent('paste', { dataType: 'text/plain', data: 'My string' } );
//  document.dispatchEvent(pasteEvent);

  def kvInputMat(kv: Array[(String, String)]) = {
    kv.zipWithIndex.foreach{
      case ((k, v), idx) =>
        ElementFactory(
          k,
          xOffset + spacingX,
          subTabOffset+subTabSpacing*idx,
          Act0,
          flag=Element.BodyObject
        )() -> {
          val elemV = ElementFactory(
            v,
            xOffset + spacingX * 2,
            subTabOffset + subTabSpacing * idx,
            () => {
              println("v " + v)
              Canvas.activeElem.foreach {
                e =>
                  val c = Input.getClipboard
                  val clipStripId = k + "=" + c.split(":").tail.mkString(":")
                  e.redrawText(clipStripId)
                  PersistentWebSocket.sendKV(k, clipStripId)
              }
            },
            flag = Element.BodyObject,
            key = v,
            setActiveOnTrigger = true,
            tabSync = false
          )()

          elemV
        }
    }
  }





  val uiAct = () => {
    Editor.apply()
    ()
  }

  val buttons : List[NavSpec] = List(
    reload,
    account,
    "UI" -> uiAct -> List[(String, Act)]()
    /*"Servers" -> {() =>
        kvInputMat(Array(
        "access",
        "secret",
        "pem"
        ).map{x => (x,x)})
      ()
    } -> List("FakeS" -> Act0)*/

  )


  object Element extends Enumeration {
    type Element = Value
    val Tab, SubTab, Status, BodyObject = Value
  }

  def tabSwitchClear() : Unit = {
    Canvas.elementTriggers.filter{
      case (elem, act) =>
        val flag = elem.flag
        flag == Element.SubTab ||
          flag == Element.BodyObject
    }.foreach{
      _._1.deregister()
    }
  }

  def makeEmail(email: String) = {
    ElementFactory(
      email,
      xOffset + spacingX*4, curY, Act0, flag=
        Element.Status
    )()
  }
  
  var emailStatus = makeEmail("yourEmail")

  def setEmail(email: String) = {
    emailStatus.deregister()
    makeEmail(email)
   }

  def setupButtons() = {

    buttons.zipWithIndex.foreach{
      case (((tabName, trigger), subTabs), bIdx) =>
        val curX = xOffset + spacingX*bIdx
        val subTabSetup = subTabs.zipWithIndex.map{
          case ((stName, stTrigger), stIdx) =>
            val curSubX = 25
            val curSubY = 120 + stIdx*subTabSpacing
            ElementFactory(
              stName, curSubX, curSubY, stTrigger,
              flag=Element.SubTab)
       }
      val exclusionTrigger = () => {
      //  println("exclusivetrigger")
          //if (Canvas.activeElem.map{_.name} == Some(tabName))
          tabSwitchClear()
          subTabSetup.foreach{sts => sts()}
          trigger()
      }
      val buttonF = ElementFactory(
        tabName, curX, curY, exclusionTrigger, flag=
      Element.Tab
      )
      val button = buttonF()
    }
  }
}
