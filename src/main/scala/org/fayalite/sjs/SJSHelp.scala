
package org.fayalite.sjs

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{Element, MouseEvent, HTMLElement}

import scala.scalajs.js.JSON


/**
  * For styled draws / fillColor on canvas context
  * Matches IntelliJ Darcula
  */
trait HTMLHexColorSchemes {

  val methodGold = "#FFC66D"
  val keywordOrange = "#CC7832"
  val bgGrey = "#2B2B2B"
  val valPurple = "#9876AA"
  val lightBlue = "#A9B7C6"
  val commentGrey = "#808080"
  val ansiGrey = "#555555"
  val ansiDarkGrey = "#1F1F1F"
  val commentGreen = "#629755"
  val superRed = "#FF0000"
  val xmlOrange = "#E8BF6A"
  val mediumBlue = "#6897BB"
  val annotationYellow = "#BBB529"

}

trait HTMLDOMHelp {

  def appendBody(s: Element) = {
    dom.document.body.appendChild(s)
  }

  def byClass(c: String) = document.getElementsByClassName(c)

  /**
    * Convenient constructors for dynamic DOM
    * rearrangement / usage
    * @param ht : HTMLElement representing a DOM node cast
    *           to the appropriate scala.js type
    */
  implicit class HTMLExtensions(ht: HTMLElement) {
    def withClick(f: MouseEvent => Unit) = {
      ht.onclick = f
      ht
    }
    def withClass(c: String) = {
      ht.className = c
      ht
    }
    def clickEach(f: => Unit) = withClick { me: MouseEvent => f }

    def withAttr(a: String, v: String) = {
      ht.setAttribute(a, v)
      ht
    }

    def withText(s: String) = {
      ht.textContent = s
      ht
    }

    def withChild(c: Element) = {
      c.appendChild(ht)
      c
    }

    /**
      * Append all elements as children and return original
      * @param c : Elements to add as children to this parent
      * @return Parent
      */
    def withChild(c: List[Element]) = {
      c.foreach {
        ht.appendChild
      }
      c
    }
  }

  /**
    * Simple button with text node
    * @param s : Text to appear on child node of button
    * @return Parent button node with child pre-appended.
    */
  def htmlButton(s: String) = {
    val obj: Element = document.createElement("button")
    val tn = document.createTextNode(s)
    obj.appendChild(tn)
    obj.asInstanceOf[HTMLElement]
  }

}


/**
  * Generic friendly helper methods oriented around
  * DOM manipulation and Scala.js inconveniences
  */
trait SJSHelp extends HTMLHexColorSchemes
 with HTMLDOMHelp {


  // Convenience aliases
  implicit class StringCommonExt(s: String) {
    def element = createElement(s)
  }
  def createElement(s: String) = document.createElement(s)

  // Use this instead of json4s. It's the javascript
  // native json serializer.
  def jsonify[T](a: Seq[T]) = JSON.stringify(a)

  /**
    * Cookie data stored on the client rendered to a KV pair
    * identified by "cookie"
    * @return JSON serialized form of Map("cookie" -> the cookie string from
    *         the document node)
    */
  def metaData = {JSON.stringify(Map("cookie" -> window.document.cookie))}

  /**
    * Javascript compatible form of time, don't rely on anything else
    * @return : Equivalent of currentTimeMillis for java or look at
    *         the javascript docs for their date format.
    */
  def time = {
    import scala.scalajs.js.Date
    new Date().getTime()
  }

}



