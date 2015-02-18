package org.fayalite.ui

import java.awt.image.RenderedImage
import javax.imageio.ImageIO

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.util.{SimpleRemoteServer, SparkReference}
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

  import scala.collection.mutable.{Map => MMap}
  

  type SenderMap = MMap[String, ActorRef]
  val allSenders = MMap[String, ActorRef]()

 // val parser = org.fayalite.ui.ParseClient.parseClient()
  
  //var parseServer = parser.getServerRef
  type SprayFrame = spray.can.websocket.frame.Frame
  
  case class WebsocketPipeMessage(senderPath: String, message: SprayFrame)
  
  case class RequestClients()
  
  class WebsocketPipe() extends Actor {
    def receive = {
      case WebsocketPipeMessage(senderPath, message) =>
        println("attempting to send client msg " + senderPath + " msg: " + message)
      allSenders.get(senderPath).foreach{
          s =>
            println("found sender")
            s ! message //.asInstanceOf[spray.can.websocket.frame.Frame]
        }
      case RequestClients() =>
        println("requestClients")
        sender ! allSenders.keys.toSet
      case _ =>
        println("websocketpipe")
    }
  }
  
  val pipePort = defaultPort + 168
  val pipeServer = new SimpleRemoteServer({new WebsocketPipe()}, pipePort)


  class WebSocketWorker(val serverConnection: ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
    override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
    import org.fayalite.repl.REPL._
    import akka.pattern.ask
    def businessLogic: Receive = {
        case x@(_: BinaryFrame | _: TextFrame) =>

         //case TextFrame(msg)
          x match {
            case TextFrame(msg) =>
         //     println("echoing " + msg.utf8String)
       /*       println(sender().path)
              println(serverConnection.path)
              println(serverConnection.path.toSerializationFormat)*/
/*              send(TextFrame(msg.utf8String))
              sender() ! TextFrame(msg.utf8String + "sender() ! x")*/
           //   allSenders(sender().path.toString) = sender()
        /*      allSenders.foreach{
                case (sp, sndr) =>
                  println("attempting to send to " + sp)
                  sndr ! TextFrame(msg.utf8String + sp)
              }*/
            case _ =>
              println("binary frame")
        //      allSenders(sender().path.toString) = sender()

          }
          allSenders(sender().path.toString) = sender()




        /*
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
        */

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
          pathPrefix("js") { get { getFromResourceDirectory("js") } } //~
        //TODO: Fix serving of pages -- testing websocket directly for now on static html.
    //      getFromResource("websocket.html") ~
/*
          pathPrefix("fayalite") { get { getFromFile("/Users/ryle/Documents/repo/fayalite/target/" +
            "scala-2.10/fayalite-fastopt.js") } } ~
        get {
          getFromFile("/Users/ryle/Documents/repo/fayalite/target/scala-2.10" +
            "index-fastopt.html")
} ~  */
/*             get{complete {
              <body>
              <h1>Say hello to spray</h1>
                <script type="text/javascript" src="http://cdn.jsdelivr.net/jquery/2.1.1/jquery.js"></script>
                <script type="text/javascript" src="js/fayalite-fastopt.js"></script>
                <script type="text/javascript">
                  tutorial.webapp.TutorialApp().main();
                </script>
              </body>
                }*/
       // }
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
