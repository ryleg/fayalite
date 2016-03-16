package org.fayalite.gate.server

import spray.can.Http

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
