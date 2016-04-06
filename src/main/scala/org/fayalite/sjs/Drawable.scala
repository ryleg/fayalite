/*
package org.fayalite.sjs

import rx.core.Var


/**
  * Contains static references for any
  * usable metadata related to drawing anywhere
  * Either Canvas or otherwise
  */
object Drawable {


}


/**
  * Something that is gonna get drawn to
  * a screen somewhere and has some universal
  * values on how that occurs
  */
trait Drawable {


  def off() = visible() = false
  def on() = visible() = true
  val visible : Var[Boolean] = Var{true}

  /**
    * Completely remove this object
    * from wherever it is being seen
    */
  def clear(): Unit

  /**
    * Draw this object where it belongs
    * according to some other definition
    */
  def draw(): Unit

  /**
    * Basically an alias for not having to clear
    * every call.
    */
  def redraw(): Unit = {
    clear(); draw()
  }

}
*/
