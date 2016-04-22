package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.LatCoord
import org.scalajs.dom._
import org.scalajs.dom.raw.{KeyboardEvent, MouseEvent}

/**
  * Finishing the Scala.js bindings by adding missing stuff
  */
trait InputHelp {

  /**
    * For processing user mouse input into Scala
    * native formats better than the current implementation
    * @param me : See javascript docs for explanations
    */
  implicit class MouseHelp(me: MouseEvent) {
    /**
      * Converter to lattice coordinates based on
      * a square sized tile
      * @param tileSize : Width / height of every square
      *                 on grid
      * @return : Lattice coordinates in absolute form
      *         rounded to the nearest edge by toInt
      */
    def tileCoordinates(tileSize: Int) = {
      val x = (me.clientX / tileSize).toInt * tileSize
      val y = (me.clientY / tileSize).toInt * tileSize
      LatCoord(x,y)
    }

  }

  /**
    * ke.key and ke.charCode are both unreliable and end
    * up being undefined a lot of the time. Use this
    * when you want to know what the user is actually
    * typing.
    * @param ke : Scala.js keyboardevent, lookup javascript docs
    *           for explanations.
    */
  implicit class KeyHelp(ke: KeyboardEvent) {
    /**
      * Return the proper (including upper/lower case) character
      * typed as a String
      * @return : String (but only a single character) -- For
      *         convenience with use of all the javascript
      *         methods that rely on string
      */
    def keyString = ke.keyCode.toChar.toString
  }

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


}
