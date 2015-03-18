package org.fayalite.util

import org.fayalite.repl.REPL.SuperInstruction
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, _}
import org.fayalite.Fayalite._


import scala.util.Try


object JSON {

  implicit val formats = DefaultFormats

  def caseClassToJson(message: Any) = {
    implicit val formats = DefaultFormats
    compactRender(Extraction.decompose(message))
  }

  def parseExtract[T](msg: String) = {
    implicit val formats = DefaultFormats
    parse(msg).extract[T]
  }

  def tryParseExtract[T](msg: String) : Option[T] = {
    Try{parseExtract[T](msg)}.printOpt
  }

  def parse4s(msg: String) = parse(msg)

  def parseSuperInstruction(msg: String) = parse(msg).extract[SuperInstruction]

  def compactRender(msg: JValue) = compact(render(msg))

//  def parseAs[T](msg: String)(implicit ev: ClassTag[T]) = parse(msg).extract[T]

}
