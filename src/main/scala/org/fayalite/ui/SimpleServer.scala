package org.fayalite.ui

import java.awt.image.RenderedImage
import javax.imageio.ImageIO

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.util.SparkReference
import org.scalajs.dom.{ArrayBuffer, Uint8Array}
import spray.can.server.UHttp
import spray.can.{Http, websocket}
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import spray.http.HttpRequest
import spray.routing.HttpServiceActor

import scala.collection.mutable
import java.awt._
import java.io._

import scala.util.Try

object SimpleServer extends App with MySslConfiguration {

  final case class Push(msg: String)

  object WebSocketServer {
    def props() = Props(classOf[WebSocketServer])
  }
  class WebSocketServer extends Actor with ActorLogging {
    def receive = {
      // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
      case Http.Connected(remoteAddress, localAddress) =>
        val serverConnection = sender()
        val conn = context.actorOf(WebSocketWorker.props(serverConnection))
        serverConnection ! Http.Register(conn)
    }
  }

  object WebSocketWorker {
    def props(serverConnection: ActorRef) = Props(classOf[WebSocketWorker], serverConnection)
  }
  class WebSocketWorker(val serverConnection: ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
    override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

    import org.fayalite.repl.REPL._
    import akka.pattern.ask

    val parser = org.fayalite.ui.ParseClient.parseClient()

    var parseServer = parser.getServerRef

    def businessLogic: Receive = {
        case x@(_: BinaryFrame | _: TextFrame) =>

          parseServer match {
            case Some(parseServerActorRef) =>
              val response = Try{
                parseServerActorRef.??[spray.can.websocket.frame.Frame](x)
              }.toOption
              response.foreach{
                r => send(r)
              }
            case None =>
              println("parse server not found on request, re-attempting connection")
              //TODO: Make this so much less dangerous
              parseServer = parser.getServerRef
          }

        case Push(msg) => {
          println("Pushmsg: " + msg + " " + TextFrame(msg))
          send(TextFrame(msg))
        }

        case x: FrameCommandFailed =>
          println("frame failed " + x)
          //log.error("frame command failed", x)

        case x: HttpRequest =>
        println("httprequest " + x)
        // do something
     // }
    }

    def businessLogicNoUpgrade: Receive = {
      implicit val refFactory: ActorRefFactory = context
      runRoute {
      //  path("hello") {
        pathPrefix("css") { get { getFromResourceDirectory("css") } } ~
          pathPrefix("js") { get { getFromResourceDirectory("js") } } ~
    //      getFromResource("websocket.html") ~
/*
          pathPrefix("fayalite") { get { getFromFile("/Users/ryle/Documents/repo/fayalite/target/" +
            "scala-2.10/fayalite-fastopt.js") } } ~
        get {
          getFromFile("/Users/ryle/Documents/repo/fayalite/target/scala-2.10" +
            "index-fastopt.html")
} ~  */
             get{complete {
              <body>
              <h1>Say hello to spray</h1>
                <script type="text/javascript" src="http://cdn.jsdelivr.net/jquery/2.1.1/jquery.js"></script>
                <script type="text/javascript" src="js/fayalite-fastopt.js"></script>
                <script type="text/javascript">
                  tutorial.webapp.TutorialApp().main();
                </script>
              </body>
                }
        }
        //getFromResourceDirectory("webapp")
    //
      }
    }
  }

  def doMain() {
    implicit val system = ActorSystem()

    val server = system.actorOf(WebSocketServer.props(), "websocket")

    IO(UHttp) ! Http.Bind(server, "localhost", 8080)

    readLine("Hit ENTER to exit ...\n")
    system.shutdown()
    system.awaitTermination()
  }

  // because otherwise we get an ambiguous implicit if doMain is inlined
  doMain()
}
