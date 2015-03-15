
package org.fayalite.ui.app.io

import org.scalajs.dom._

/**
 * Hacks to avoid using serializers like upickle / etc. temporarily while testing.
 * All need to be changed to case classes.
 */
object InputCapture {

  var ws: WebSocket = _

  def registerInputListeners(ws_ : WebSocket) = {
    ws = ws_
  //  regML
  }

  object s{
    def s(msg: String) = ws.send(msg)
  }

  object i {
    def m (k: String) = (me: MouseEvent) => s s s"$k,${me.clientX},${me.clientY}"
    def k(kt: String) = (ke: KeyboardEvent) => s s s"$kt,${ke.key}"
  }

  def regML {
    window.onmousemove = i m "move"
/*    window.onmousedown = i m "down"
    window.onmouseup = i m "up"
    window.onkeydown = i k "down"
    window.onkeyup = i k "up"*/
  }
}
