package org.fayalite.gate.server

import java.io.File

import akka.actor.{ActorRefFactory, ActorRef, Props, ActorSystem}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.Http
import rx._
import spray.routing
import spray.routing.{RoutingSettings, StandardRoute, HttpServiceActor}

import scalatags.Text.all._

/**
  * A disposable / changeable message processing
  * loop for deployed actors. Dead simple, no
  * side effects allowed. Process loop in a vacuum
  */
trait MessageProcesser extends spray.routing.Directives {

  // For making getFromFile work

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
  val route: Var[(akka.actor.ActorRefFactory => routing.Route)]

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
  val sjsFastOptMap = new File(targetS210Path, "fayalite-fastopt.js.map")
  val sjsFastOptDep = new File(targetS210Path, "fayalite-jsdeps.js")

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

    val dbg = // "<!DOCTYPE html>" +
      html(
        scalatags.Text.all.head(
          scalatags.Text.tags2.title("fayalite"),
          meta(charset := "UTF-8")
        )
        ,
        body(
          script(
            src := "./target/scala-2.10/fayalite-fastopt.js",
            `type` := "text/javascript"),
          script("org.fayalite.sjs.App().main()",
            `type` := "text/javascript")
        )
      ).render

    /**
      * Testable route to serve a rendered page
      * override or reassign for real-time change
      */
    val route = Var { (ctx: ActorRefFactory) => {
      import scalatags.Text.all._
      import fa._
      {
        val defaultRoute = get {
          completeWith(
            s"""
               |<!DOCTYPE html>
               |<html>
               |  <head>
               |    <meta charset="UTF-8">
               |    <title>fayalite</title>
               |  </head>
               |  <body>
               |    <script type="text/javascript" src="fayalite-fastopt.js"></script>
               |    <script type="text/javascript"> org.fayalite.sjs.App().main();</script>
               |  </body>
               |</html>
            """.stripMargin
          )
        }
        implicit val refFactory = ctx

        pathPrefix("fayalite") {
          println("Path prefix fayalaite")
          get {
            println("fayget")
            unmatchedPath { path =>
              println("unmatched path " + path)
              val f = new File(targetS210Path, "fayalite" + path.toString)
              println( " new file " + f.getCanonicalPath)
              getFromFile(f)
            }
          }
        } ~ defaultRoute

      }
    }
    }
  }

    // unmatchedPath {
    //    path =>

    // post {
    //   val x = extract {_.request.entity.asString


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
