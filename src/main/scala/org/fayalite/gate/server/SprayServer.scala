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


trait MessageProcesser {

  val process: Var[
    ((String, ActorRef) =>
      Unit)]
}

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
    *
    * @return : Route to run, see spray docs for examples
    *         like
    *         getFromFile("index.html") ~  // put your rest API here after this tilda
    */
  def route : routing.Route = {
    get {
      complete {
        "yo"
      }
    }
  }

  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: akka.actor.ActorRefFactory = context
    runRoute { route }
  }
}


/**
  * All this does is pass over a registered
  * HTTP connection after it's been established to
  * another actor. This doesn't really need to be used
  * other than for starting up a server as the primary gateway
  * actor, let the other actors handle the registered connections and
  * messages
  *
  * @param process : process handling messages after connection
  *              established
  */
class ConnectionRegistrationHandler(
                                     process: MessageProcesser
                                   )
  extends akka.actor.Actor
    with akka.actor.ActorLogging {
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val p = akka.actor.Props(
        classOf[WebSocketWorkerLike],
        serverConnection,
        process
      )
      val conn = context.actorOf(p)
      serverConnection ! Http.Register(conn)
  }

}


class SprayServer(port: Int = 8080) {
  val system = ActorSystem()

  val mp = new MessageProcesser {
    val process = Var { (a: String, b: ActorRef) => {
      println("Recieved msg: " + a + " from " + b.path)
      b ! "Response"
    }
    }
  }

  /**
    * Reassigns new connections to a different process
    *
    * @param a : process to receive connections
    */
  def resetProcess(a: ((String, ActorRef) =>
    Unit)) = mp.process() = a

  val server = system.actorOf(
    Props(
      classOf[ConnectionRegistrationHandler],
      mp
    ),
    "ConnectionRegistrationHandler"
  )

  IO(UHttp)(system) ! Http.Bind(server, "0.0.0.0", port)

}

object SprayServer {
  def main(args: Array[String]) {
    new SprayServer()
  }
}
