package org.fayalite.util.server

import akka.actor.{Props, ActorSystem, Actor}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.TextFrame
import spray.can.{websocket, Http}
import spray.http.HttpRequest
import spray.routing.HttpServiceActor

/**
  * Created by aa on 3/2/2016.
  */


abstract class WebSocketWorkerLike(
                                 val serverConnection: akka.actor.ActorRef
                               ) extends HttpServiceActor
  with websocket.WebSocketServerWorker {
  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
  def businessLogic: Receive = {
    case TextFrame(xq) =>
      val socketMsgStr = xq.utf8String
    case x: FrameCommandFailed =>
      println("frame failed " + x)
    case x: HttpRequest =>
      println("httprequest " + x)
    case x => println("unrecognized frame" + x)
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
  * another actor.
  * @param actor : Actor handling messages after connection
  *              established
  */
abstract class SprayServerLike(actor: Actor)
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

class SprayServerZero(a: Actor) extends SprayServerLike(a) {

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