package org.fayalite.repl



import org.json4s.jackson.JsonMethods._
import org.json4s.{Extraction, JsonAST, DefaultFormats}
import org.json4s.JsonDSL._
import org.json4s._

import scala.reflect.ClassTag


object JSON {

  implicit val formats = DefaultFormats

  def caseClassToJson(message: Any) = {
    implicit val formats = DefaultFormats

    compact(render(Extraction.decompose(message)))
  }

  def parse4s(msg: String) = parse(msg)

//  def parseAs[T](msg: String)(implicit ev: ClassTag[T]) = parse(msg).extract[T]

}
