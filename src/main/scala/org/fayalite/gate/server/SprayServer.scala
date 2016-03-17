package org.fayalite.gate.server

import java.io.File

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.Http
import rx._
import spray.routing
import spray.routing.{StandardRoute, HttpServiceActor}

/**
  * A disposable / changeable message processing
  * loop for deployed actors. Dead simple, no
  * side effects allowed. Process loop in a vacuum
  */
trait MessageProcesser extends spray.routing.Directives {

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

  /**
    * Allows use of ScalaTags to render HTML
    * and complete a get request with a String of full html
    * for the page render
    *
    * @param html : Full HTML of page to render as if you
    *             read it from index.html
    * @return : Route with response.
    */
  def completeWith(html: String): StandardRoute = {
    import spray.http.MediaTypes._
    respondWithMediaType(`text/html`) & complete {
      html
    }
  }
  def completeWithJS(js: String): StandardRoute = {
    import spray.http.MediaTypes._
    respondWithMediaType(`application/javascript`) & complete {
      js
    }
  }
}

/**
  * Simple box around Spray to get a quick
  * server up with WS / REST
  *
  * @param port : To bind on, assumes open host ip
  */
class SprayServer(
                   port: Int = 8080
                 ) {

  val currentPath = new File(".")
  val targetPath = {
    new File(currentPath, "target")
  }
  val targetS210Path = new File(targetPath, "scala-2.10")
  val sjsFastOpt = new File(targetS210Path, "fayalite-fastopt.js")

  /**
    * Barebones actor system to get you started
    * override with your choice
    */
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

    /**
      * Testable route to serve a rendered page
      * override or reassign for real-time change
      */
    val route = Var {
      import scalatags.Text.all._
      import fa._
      {
        get {
          completeWith(
            html(
              scalatags.Text.all.head(
                scalatags.Text.tags2.title("fayalite"),
                meta(charset := "UTF-8")
                )
               ,
              body(
                script(
                  readLines(sjsFastOpt.getCanonicalPath).mkString("\n"),
                `type`:="application/javascript"),
                  script("org.fayalite.sjs.App().main()",
                    `type`:="text/javascript")
              )
            ).render
          )
        }
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
