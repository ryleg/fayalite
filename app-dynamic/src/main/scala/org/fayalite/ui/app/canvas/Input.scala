package org.fayalite.ui.app.canvas

import org.scalajs.dom

class Input {

}

object Input {

  def getClipboard = {
    val clip = dom.document.body
      .getElementsByTagName("div")(1).textContent
    println("clipboard: " + clip)
    clip
  }

}