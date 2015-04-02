package org.fayalite.ui.app

import org.fayalite.ui.app.canvas.Schema.ParseResponse
import rx._

class StateSync {


}

object StateSync {
  import PersistentWebSocket._
  lazy val parseResponse = Rx {
    import upickle._
    val str = messageStr()
    read[ParseResponse](str)
  }
  def processBridge(bridge: String) = {
    bridge
  }

}