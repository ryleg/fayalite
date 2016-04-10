package org.fayalite.agg

import ammonite.ops.Path
import fa.Schema
import Schema.ParsedExtr

trait ParserLike {

  var extractorGroups = List[(String, List[(String, String)])]()

  var firstElemAttrExtractors = List[(String, String)]()

  /*
  def readExtrParse[T](path: Path, parser: ParsedExtr => Traversable[T]
                      ) = path.jsonRec[Extr].flatMap { case Extr(url, qq) =>
    qq.map {
      q => parser(ParsedExtr(url, q.soup))
    }
  }.toList.flatten
*/


}
