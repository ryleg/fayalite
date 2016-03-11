package org.fayalite.ui.j2d

import java.awt.{GraphicsEnvironment, GraphicsDevice, Frame}
import java.awt.event.WindowEvent


/**
  * Creates a fullscreen window with a workaround
  * to not minimize to the background, flickers a
  * little on context change but whatever, beats alternative
  *
  * @param windowId: String of 0,1,2 etc. corresponding to monitor id
  */
class FullScreenFrame(windowId: String) extends FFrame(
  name = windowId
) {
  /**
    * This causes a little flicker on context
    * switching but it restores the max state, otherwise
    * the window will just dissapear.
    *
    * @param we : Some window event, like the user clicked on a different
    *           monitor.
    */
  override def processWindowEvent (we: WindowEvent): Unit = {
    setExtendedState (getExtendedState | Frame.MAXIMIZED_BOTH)
  }

  /**
    * This is for assigning the monitor on discovery
    * in case you ever need to use the reference
    * to the monitor device for grabbing info about it.
    */
  var graphicsDevice: GraphicsDevice = null

  override def init() = {
    setExtendedState(getExtendedState)
    setAlwaysOnTop(true)
    setUndecorated(true)
    super.init()
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
      case q if q.getIDstring.contains(windowId) =>
        q.setFullScreenWindow(this) // This triggers fullscreen
        graphicsDevice = q // for grabbing window info.
      case _ => // Assign errors through overrides if requested.
    }
  }

}
/*

      override def processWindowEvent(we: WindowEvent): Unit = {
       // if (we.getID == WindowEvent.WINDOW_DEACTIVATED) {
          //   this.setState(Frame.NORMAL)
          //    this.requestFocus()
          setExtendedState(getExtendedState | Frame.MAXIMIZED_BOTH)
    super.processWindowEvent(we)*/
