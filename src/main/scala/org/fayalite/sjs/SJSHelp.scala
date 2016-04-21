
package org.fayalite.sjs

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{Element, MouseEvent, HTMLElement}

import scala.scalajs.js.JSON


trait SJSHelp {

  val methodGold = "#FFC66D"
  val burntGold = "#CC7832"
  val bgGrey = "#2B2B2B"
  val lightBlue = "#A9B7C6"
  val commentGrey = "#808080"
  val ansiGrey = "#555555"
  val ansiDarkGrey = "#1F1F1F"
  val commentGreen = "#629755"
  val superRed = "#FF0000"
  val xmlOrange = "#E8BF6A"
  val mediumBlue = "#6897BB"
  val annotationYellow = "#BBB529"

  implicit class StringCommonExt(s: String) {
    def element = createElement(s)
  }

  def createElement(s: String) = document.createElement(s)

  def jsonify[T](a: Seq[T]) = JSON.stringify(a)

  def metaData = {
    JSON.stringify(Map("cookie" -> window.document.cookie))
  }

  def appendBody(s: Element) = {
    dom.document.body.appendChild(s)
  }

  def byClass(c: String) = document.getElementsByClassName(c)


  implicit class HTMLExtensions(ht: HTMLElement) {

    def withClick(f: MouseEvent => Unit) = {
      ht.onclick = f
      ht
    }

    def withClass(c: String) = {
      ht.className = c
      //      ht.setAttribute("class", c)
      ht
    }

    def clickEach(f: => Unit) = withClick { me: MouseEvent => f }

    def withAttr(a: String, v: String) = {
      ht.setAttribute(a, v);
      ht
    }

    def withText(s: String) = {
      ht.textContent = s;
      ht
    }

    def withChild(c: Element) = {
      c.appendChild(ht)
      c
    }

    def withChild(c: List[Element]) = {
      c.foreach {
        ht.appendChild
      }
      c
    }

  }

  def htmlButton(s: String) = {
    val obj: Element = document.createElement("button")
    val tn = document.createTextNode(s)
    obj.appendChild(tn)
    obj.asInstanceOf[HTMLElement]
  }

  def time = {
    import scala.scalajs.js.Date
    new Date().getTime()
  }

}



