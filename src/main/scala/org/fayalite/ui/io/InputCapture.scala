package org.fayalite.ui.io

import org.scalajs.dom._

object InputCapture {

  def regML {
    window.onmousemove = i m "move"
    window.onmousedown = i m "down"
    window.onmouseup = i m "up"
    window.onkeydown = i k "down"
    window.onkeyup = i k "up"
  }
}
