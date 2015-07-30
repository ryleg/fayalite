package org.fayalite.agg

trait ParserLike {

  var extractorGroups = List[(String, List[(String, String)])]()

  var firstElemAttrExtractors = List[(String, String)]()


}
