package org.fayalite.sjs.input

import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode

/**
  * Setup listeners for inputs from client
  * in terms of mouse / key actions
  */
object InputBootstrap {

  /**
    * Prevents browser specific right click context
    * menu popup. For custom rendering by canvas
    * of right click handles
    */
  def disableRightClick(): Unit = {
    window.oncontextmenu = (me: MouseEvent) => {
      me.preventDefault()
    }
  }

  def init() : Unit = {
    disableRightClick()

    window.onkeydown = (ke: KeyboardEvent) => {

    }
    window.onkeyup = (ke: KeyboardEvent) => {

    }

  }
}
