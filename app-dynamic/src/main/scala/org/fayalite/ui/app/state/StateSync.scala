package org.fayalite.ui.app.state

import org.fayalite.ui.app.comm.PersistentWebSocket
import org.fayalite.ui.app.manager.Editor

class StateSync {


}

object StateSync {

  def initializeApp() = {

    PersistentWebSocket.pws
    Editor.editor

  }

  def processBridge(bridge: String) = {
    initializeApp()
    bridge
  }
}