package org.fayalite.ui.app.canvas

import org.scalajs.dom

class Input {

}

/**
 * Need to figure out a workaround to access clipboard data directly.
 * Wasn't available in previous scala.js versions, maybe now or soon?
 */
object Input {

  /**
   * Get an HTML element's text that auto-updates with paste contents.
   * @return Pasted string
   */
  def getClipboard = {
    val clip = dom.document.body
      .getElementsByTagName("div")(1).textContent
    println("clipboard: " + clip)
    clip
  }

}