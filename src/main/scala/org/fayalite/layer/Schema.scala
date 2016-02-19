package org.fayalite.layer

object Schema {

  case class FixedName(
                        clean: String,
                        original: String,
                        words: List[String]
                      )

}
