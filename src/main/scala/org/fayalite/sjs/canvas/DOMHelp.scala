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

  /**
    * Adjusted width with a clip to prevent scroll bar appearing
    * Hack for getting to display properly in desktop browser,
    * untested elsewhere
    * @return : Clip adjusted pixel width of viewport
    */
  def w = document.documentElement.clientWidth - 18 // wtf? it makes a scroll bar without this offset

  /**
    * Adjusted height with clip margin for preventing scrollbars
    * WARNING: Complete hacks here
    * @return : Adjusted pixel height
    */
  def h = document.documentElement.clientHeight - 50

}
