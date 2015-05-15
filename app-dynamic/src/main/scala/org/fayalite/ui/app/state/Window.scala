package org.fayalite.ui.app.state

import org.scalajs.dom._

import scala.scalajs.js.JSON

object Window {

  def metaData = {
    JSON.stringify(Map("cookie" -> window.document.cookie))
    //window.document.cookie
  }

}
