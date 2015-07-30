package org.fayalite.ui.app.state

import org.fayalite.ui.app.canvas.{Canvas, Schema}
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

  case class FileIO(name: String, contents: String)
  case class IdIO(id: Int, io: String)
  case class RIO(asyncOutputs: Array[String], asyncInputs: Array[IdIO])
  case class Response(
                       classRefs: Option[Array[String]],
                       files: Option[Array[FileIO]],
                       replIO: Option[RIO]
                       )

  case class ParseRequest (
                          code: Option[String]
                      //    pollOutput: Boolean = true
                            )

  def initializeApp() = {

    CellManager.onLoad()
    Canvas.initCanvas()
    println(Input.t)
    println(Editor.editor)
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

  def code(s: String) = {
    val kvs = ParseRequest(
      Some(s)
    )
    import upickle._
    val ser = write(kvs)
    import Disposable.send
    println("sent : " + ser)
    send(ser)
  }

  /*
  Wow, theres an un-compilable state here, not sure which
  variation it is, but I tried pulling above into sendCase(a: Any)
  and it never compiled. That or flashRate heartbeats caused it
  to lock forever on fastOptJS
   */

  Input.flashRate.foreach{
    e =>
     import upickle._
      PersistentWebSocket
        .send(write(ParseRequest(code=None)))

  }

  import rx.ops._


  case class ParseResponse(sbtOut: String)

/*  val response = Var(ParseResponse(""))

  val listener = Obs(PersistentWebSocket.pws.messageStr, skipInitial = true) {
    val m = PersistentWebSocket.pws.messageStr()
    import upickle._
    response() = read[ParseResponse](m)
  }*/


  val ms = PersistentWebSocket.pws.messageStr
  ms.foreach{println}
  def processBridge(bridge: String) = {
   // initializeApp()
    code("println(150)")
    bridge
  }
}