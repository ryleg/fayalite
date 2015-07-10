package org.fayalite.ui.app.state

import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.comm.{Disposable, PersistentWebSocket}
import org.fayalite.ui.app.manager.Editor
import org.fayalite.ui.app.text.CellManager
import rx._

import scala.scalajs.js.{JSON, Dynamic}
import scala.util.Try

class StateSync {


}

object StateSync {

  val parsedMessage = Var(null.asInstanceOf[Dynamic])
  val meta = Var(null.asInstanceOf[Response])

  case class Response(classRefs: Array[String])

  def initializeApp() = {
    CellManager.onLoad()

    // EXPERIMENTAL BELOW
    val resp = Disposable.send("yo")
    import rx.ops._
    resp.foreach{q =>
      println("yo response: " + q)
      Try{
        import upickle._
        meta() = read[Response](q)
      }
      parsedMessage() = JSON.parse(q)
    }
  }

  def processBridge(bridge: String) = {
    initializeApp()
    bridge
  }
}