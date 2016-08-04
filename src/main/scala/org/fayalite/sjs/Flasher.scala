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

  // TODO : NOT THIS its just an example don't judge.
  def reactIsValModifier(c: String, t: CanvasContextInfo) = {
    if (c == "l" &&
      indexLatResolve.get(t.latCoords.left)
        .exists {
          _.text.exists {
            _ == "a"
          }
        } &&
      indexLatResolve.get(t.latCoords.left.left)
        .exists {
          _.text.exists {
            _ == "v"
          }
        } &&
      indexLatResolve.get(t.latCoords.left.left.left).isEmpty
    ) {
      Seq(
        t,
        indexLatResolve.get(t.latCoords.left).get,
        indexLatResolve.get(t.latCoords.left.left).get
      ).foreach {
        z =>
          z.context.clearRect(0D, 0D, z.tileSize, z.tileSize)
          z.drawText(z.text.get, keywordOrange)
      }
    }
  }

*/
