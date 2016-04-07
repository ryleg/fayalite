/*
package org.fayalite.sjs

import rx._


/**
  * An element that blinks on and off
  * repeatedly such as a cursor.
  */
trait Flasher extends Drawable {

  import rx.ops._

  val flash = Var(true)

  val o = Obs(Input.flashRate) {
    if (flash()) {
      val prev = !visible()
      visible() = prev
    }
  }
  flash.foreach{
    f =>
      if (!f) visible() = true
  }

}
*/
