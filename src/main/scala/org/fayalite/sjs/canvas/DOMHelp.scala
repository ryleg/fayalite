package org.fayalite.sjs.canvas

import org.scalajs.dom.document


/**
  * For accessing quick DOM info relevant to
  * canvas parameters / positioning /
  * client info
  */
trait DOMHelp {

  /**
    * Get the viewscreen for the client with measurements for
    * determining screen height to determine canvas tiling
    * requirements
    * @return : Rect for inspection of client viewscreen parameters
    */
  def getRect = document.body.getBoundingClientRect()

  def w = document.documentElement.clientWidth

  def h = document.documentElement.clientHeight

}
