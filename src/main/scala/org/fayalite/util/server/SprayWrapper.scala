package org.fayalite.util.server

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.TextFrame
import spray.can.{websocket, Http}
import spray.http.HttpRequest
import spray.routing.HttpServiceActor



/** NOTE : Ignores unrecognized WS frames / HTTPRequests thru socket
  * Simple wrapper around a websocket supporting
  * Spray webserver with ability to easily implement POST
  * requests using convenient helpers
  * @param serverConnection : Whatever is passing a registered
  *                         HTTP connection over here
  * @param process : Do something with websocket messages
  * @param error : Do something with websocket errors
  */
class WebSocketWorkerLike(
                                 val serverConnection: akka.actor.ActorRef,
                         process: (String, ActorRef) => Unit,
                                 error: (FrameCommandFailed => Unit) = _ => ()
                               ) extends HttpServiceActor
  with websocket.WebSocketServerWorker {
  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
  def businessLogic: Receive = {
    // This is websocket garbage
    case TextFrame(xq) =>
      val socketMsgStr = xq.utf8String // You're pretty much always
      // Just gonna be using the utf8String so unless that's an issue
      // just use this
      process(socketMsgStr, sender())
    case x: FrameCommandFailed => error(x)
    case x: HttpRequest => ()
    case x => ()
  }
  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: akka.actor.ActorRefFactory = context
    runRoute {
      getFromFile("index.html") // ~ put your rest API here after this tilda
    }
  }
}


/**
  * All this does is pass over a registered
  * HTTP connection after it's been established to
  * another actor. This doesn't really need to be used
  * other than for starting up a server as the primary gateway
  * actor, let the other actors handle the registered connections and
  * messages
  * @param actor : Actor handling messages after connection
  *              established
  */
class SprayServerLike(actor: Actor)
  extends akka.actor.Actor
    with akka.actor.ActorLogging {
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val p = akka.actor.Props(actor)
      val conn = context.actorOf(p)
      serverConnection ! Http.Register(conn)
  }
}

import rx._
import rx.ops._


class SprayWrapper(sprayLike: WebSocketWorkerLike, port: Int = 8080) {
  val system = Var(ActorSystem())
  val server = system.map{_.actorOf(Props(sprayLike), "websocket")}
  Obs(server){IO(UHttp)(system()) ! Http.Bind(server(), "0.0.0.0", port)}

}

object SprayWrapper {
  def main(args: Array[String]) {
   // new SprayWrapper()
  }
}