package org.fayalite.util

import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, _}


import scala.util.Try

/**
 * The hope of this object is that you never need to look at json4s docs again
 * We'll see how far it gets.
 */
object JSON {

  implicit val formats = DefaultFormats

  def caseClassToJson(message: Any) = {
    implicit val formats = DefaultFormats
    compactRender(Extraction.decompose(message))
  }

  def parse4s(msg: String) : JValue = parse(msg)

  def compactRender(msg: JValue) = compact(render(msg))

}
