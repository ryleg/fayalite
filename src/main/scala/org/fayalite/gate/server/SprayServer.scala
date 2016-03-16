package org.fayalite.gate.server

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.Http
import rx._
import spray.routing
import spray.routing.HttpServiceActor

/**
  * A disposable / changeable message processing
  * loop for deployed actors. Dead simple, no
  * side effects allowed. Process loop in a vacuum
  */
trait MessageProcesser extends HttpServiceActor {

  /**
    * Reassign to this to fill out your server deployment
    * with some effect processed on Websocket UTF-8 msgs
    */
  val process: Var[
    ((String, ActorRef) =>
      Unit)]

  /**
    * The route for REST requests to be processed
    */
  val route: Var[routing.Route]

}

/**
  * Simple box around Spray to get a quick
  * server up with WS / REST
  *
  * @param port : To bind on, assumes open host ip
  */
class SprayServer(port: Int = 8080) {

  val system = ActorSystem()

  /**
    * Event reaction loop for incoming messages
    * UTF-8 decoded message from a WS and the corresponding
    * sender to return a response to
    */
  val messageProcesser = new MessageProcesser {
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
    Unit)) = messageProcesser.process() = a

  /**
    * Handler that issues incoming connections new actors
    * deployed with message processer
    */
  val server = system.actorOf(
    Props(
      classOf[ConnectionRegistrationHandler],
      messageProcesser
    ),
    "ConnectionRegistrationHandler"
  )

  /**
    * Bind the port and serve connections to our
    * pre-initialized simple ActorSystem and actor
    */
  def start(): Unit = {
    IO(UHttp)(system) ! Http.Bind(server, "0.0.0.0", port)
  }

  // Automatically start, pretty typical since our
  // thread management is at the connection message deploy level.
  // Try and rely on ConnectionReg handler unless you need
  // something complicated
  start()

}

/**
  * Run for simple demonstration, check localhost:8080 for a rendered
  * page
  */
object SprayServer {
  def main(args: Array[String]) {
    new SprayServer()
  }
}
