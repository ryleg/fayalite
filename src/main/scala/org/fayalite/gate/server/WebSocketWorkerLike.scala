package org.fayalite.gate.server

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.TextFrame
import spray.can.{websocket, Http}
import spray.http.HttpRequest
import spray.routing
import spray.routing.HttpServiceActor
import rx._
import rx.ops._

/** NOTE : Ignores unrecognized WS frames / HTTPRequests thru socket
  * Simple wrapper around a websocket supporting
  * Spray webserver with ability to easily implement POST
  * requests using convenient helpers
  *
  * @param mp : Do something with websocket messages
  * //@param error : Do something with websocket errors
  */
class WebSocketWorkerLike(
                           val serverConnection: akka.actor.ActorRef,
                           mp: MessageProcesser //,
                  //           error: (FrameCommandFailed => Unit) = _ => ()
                         ) extends HttpServiceActor
  with websocket.WebSocketServerWorker {
  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
  def businessLogic: Receive = {
    // This is websocket garbage
    case TextFrame(xq) =>
      val socketMsgStr = xq.utf8String // You're pretty much always
      // Just gonna be using the utf8String so unless that's an issue
      // just use this
      mp.process()(socketMsgStr, sender())
    case x: FrameCommandFailed => //error(x)
    case x: HttpRequest => ()
    case x => ()
  }

  /**
    * Put your simple route here, this is just to avoid
    * the below spray implicit declarations on implementation
    * so you can use a Var() to reassign something side
    * effect less
    *
    * @return : Route to run, see spray docs for examples
    *         like
    *         getFromFile("index.html") ~  // put your rest API here after this tilda
    */
  def route : routing.Route = mp.route()

  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: akka.actor.ActorRefFactory = context
    runRoute { route }
  }
}
