package org.fayalite.sjs

import org.scalajs.dom

/**
  * Created by aa on 3/17/2016.
  */
object Schema {

 // {
    import upickle._
    // json.read // json.write
 // }

  case class ParseRequest (
                            code: String,
                            cookies: String,
                            requestId: String
                          )

}
