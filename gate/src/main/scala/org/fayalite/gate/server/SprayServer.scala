package org.fayalite.gate.server

import java.io.File

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.fs.FSCodePull
import spray.can.server.UHttp
import spray.can.Http
import rx._
import spray.routing

/**
  * A disposable / changeable message processing
  * loop for deployed actors. Dead simple, no
  * side effects allowed. Process loop in a vacuum
  */
trait MessageProcesser
  extends RoutingCompletions
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

}

object SpraySchema {
  case class FileRequest(file: String)
  case class FileResponse(files: Array[String])
}

/**
  * Simple box around Spray to get a quick
  * server up with WS / REST
  *
  * NOTE: If you wish to serve .js files directly as responses
  * (for executing different user's .js versions,) instead of
  * attempting to compile project with different name -- look
  * for //# sourceMappingURL=fayalite-fastopt.js.map at the bottom
  * of the fastOptJS output and change to reflect where you are serving
  * the map
  *
  * @param port : To bind on, assumes open host ip
  */
class SprayServer(
                   port: Int = 8080
                 ) {


  // TODO : Fix all this junk, I was testing various ways to render
  // the page and some of these things are relics of those tests.
  // need to make sure everything works and simplify it back to using
  // scala-tags & deal with the source mapping issue related to debugging
  // when attempting to render the whole JS as a single return string.
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

    /**
      * Testable route to serve a rendered page
      * override or reassign for real-time change
      */
    val route = Var { (ctx: ActorRefFactory) => {
      {
        implicit val refFactory = ctx
        val defaultRoute = completeWith(PageRender.defaultIndexPage) // getFromFile("index.html")
        pathPrefix("fayalite") {
          get {
            unmatchedPath { path =>
              println("unmatched path " + path)
              val f = new File(targetDir, "fayalite" + path.toString)
              println( " new file " + f.getCanonicalPath)
              getFromFile(f)
            }
          }
        } ~
          path("files") {
            get {
              import fa._
              completeWithJSON(FSCodePull.getTopLevelFiles.map{_.getName}.json)
            } ~
            post {
              def rawJson = extract { _.request.entity.asString}
              rawJson { j =>
                import fa._
                val r = j.json[SpraySchema.FileRequest]
                println("POST r", r)
                val topFiles = FSCodePull.getTopLevelFiles.collectFirst{
                  case x if x.getName == r.file =>
                    x.listFiles().toSeq.map{_.getName}
                }.getOrElse(Seq())
                val jsonResponse: String = SpraySchema.FileResponse(topFiles.toArray).json
                completeWithJSON(jsonResponse)
              }
            }
          } ~
          defaultRoute

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