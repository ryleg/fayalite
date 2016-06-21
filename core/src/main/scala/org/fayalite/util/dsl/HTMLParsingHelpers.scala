package org.fayalite.util.dsl

import fa._
import Schema.Cookie
import org.fayalite.util.JSON
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.JavaConversions

/**
  * JSoup is a pain to use within Scala
  * These are DSL like additions for convenient
  * parser description.
  */
trait HTMLParsingHelpers {

  /**
    * When extracting bulk text from a page,
    * reducing a collection of elements to
    * text is a pretty common unit operation
    * here simplified with blank space
    * delimiters, change to implicit override
    * on delimiters through override if unacceptable
    *
    * @param el: Collection of to-be reduced to
    *          text HTML nodes
    */
  implicit class ParseExt(el: List[Element]) {
    def text = el.map{_.text}.mkString(" ")
  }

  import JavaConversions._

  implicit class NodeSelectionHelp(e: Element) {
    def sel(sll: String) = e.select(sll).iterator.toList
    def fsel(sll: String) = e.select(sll).first()
    def textBy(sll: String) = e.select(sll).first().text
  }
  implicit class ElementsConverter(es: Elements) {
    def toScala = es.toIterator.toSeq
  }

  implicit class DocOps(d: Document) {
    def sel(sll: String) = d.select(sll).iterator.toList
    def fsel(sll: String) = d.select(sll).first()
  }

  implicit class HTMLStrExt(s: String) {
    val doc = Jsoup.parse(s)
    def soup = Jsoup.parse(s)
    def selText(elem: Element, sll: String) = elem.sel(sll)
      .map {_.text}
    def json[T]()(implicit m: Manifest[T]): T = {
      import JSON._
      JSON.parse4s(s).extract[T]
    }
    def domain = s.split("http://").tail.mkString.split("/").head


  }



  def parseCookiesFromFile(s: String) =
    readFromFile(s).json[List[Cookie]]


}
