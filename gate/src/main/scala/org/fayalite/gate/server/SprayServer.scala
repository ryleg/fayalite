package org.fayalite.gate.server

import java.io.File

import akka.actor.{ActorRefFactory, ActorRef, Props, ActorSystem}
import akka.io.IO
import spray.can.server.UHttp
import spray.can.Http
import rx._
import spray.routing
import spray.routing.{RoutingSettings, StandardRoute, HttpServiceActor}


/**
  * A disposable / changeable message processing
  * loop for deployed actors. Dead simple, no
  * side effects allowed. Process loop in a vacuum
  */
trait MessageProcesser
  extends spray.routing.Directives // For making getFromFile work
{

  /**
    * Reassign to this to fill out your server deployment
    * with some effect processed on Websocket UTF-8 msgs
    */
  val process: Var[((String, ActorRef) => Unit)]

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

  /**
    * Same idea as above but allows spray to complete directly
    * with JS string file contents
    * @param js : File contents of a standard .js as a single string
    * @return : Routing directive allowing that .js to be served
    *         avoiding the use of getFromFile to allow .js to be served
    *         out of a virtual file system / DB
    */
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
  val targetDir = new File(targetPath, "scala-2.11")
  val sjsFastOpt = new File(targetDir, "fayalite-fastopt.js")
  val sjsFastOptMap = new File(targetDir, "fayalite-fastopt.js.map")
  val sjsFastOptDep = new File(targetDir, "fayalite-jsdeps.js")

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

    val dbg = {
      import scalatags.Text.all._

      // "<!DOCTYPE html>" +
      html(
        scalatags.Text.all.head(
          scalatags.Text.tags2.title("fayalite"),
          meta(charset := "UTF-8")
        )
        ,
        body(
          script(
            src := "fayalite-fastopt.js",
            `type` := "text/javascript"),
          script("org.fayalite.sjs.App().main()",
            `type` := "text/javascript")
        )
      ).render
    }

    /**
      * Testable route to serve a rendered page
      * override or reassign for real-time change
      */
    val route = Var { (ctx: ActorRefFactory) => {
      {
        implicit val refFactory = ctx
        val defaultRoute = completeWith(dbg) // getFromFile("index.html")
        pathPrefix("fayalite") {
          get {
            unmatchedPath { path =>
              println("unmatched path " + path)
              val f = new File(targetDir, "fayalite" + path.toString)
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