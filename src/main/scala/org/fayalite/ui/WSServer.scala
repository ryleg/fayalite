package org.fayalite.ui

import java.awt.image.RenderedImage
import java.util.Random
import javax.imageio.ImageIO

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.ui.io.ImagePipe
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.util.{JSON, RemoteClient, SimpleRemoteServer, SparkReference}
import org.scalajs.dom.{ArrayBuffer, Uint8Array}
import spray.can.server.UHttp
import spray.can.{Http, websocket}
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{TextFrame, BinaryFrame}
import spray.http.{HttpCookie, HttpResponse, HttpRequest}
import spray.routing.HttpServiceActor

import scala.collection.mutable
import java.awt._
import java.io._

import scala.concurrent.Future
import scala.io.Source
import scala.util
import scala.util.Try

object WSServer extends App with MySslConfiguration {

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
        allSenders.foreach{
          case (sp, s) =>
            println("found sender")
            s ! message //.asInstanceOf[spray.can.websocket.frame.Frame]
        }
     /* allSenders.get(senderPath).foreach{
          s =>
            println("found sender")
            s ! message //.asInstanceOf[spray.can.websocket.frame.Frame]
        }*/
      case RequestClients() =>
        println("requestClients")
        sender ! allSenders.keys.toSet
      case _ =>
        println("websocketpipe")
    }
  }
  
  val pipePort = defaultPort + 168
  val pipeServer = new SimpleRemoteServer({new WebsocketPipe()}, pipePort)

  var started = false
  case class TestEval(flag: String, code: String)

  var parseServerRequestor = new RemoteClient()
  
  var parseServer : Option[ActorRef] = _
  def connectParseServer() = {
    parseServer =  parseServerRequestor.getServerRef(20000)
    parseServer match {
      case Some(x) => println("found parse server")
      case _ => "didnt find parse server"
    }
  }
 connectParseServer()
  import org.fayalite.repl.REPL._
  import akka.pattern.ask

  def attemptParse(msg: String, sender: ActorRef) = {
    println("attempting parse " + msg)
    parseServer match {
      case None => connectParseServer()
      case Some(ps) =>
        println("sending parse request")
        Try{ps.??[TextFrame](msg, timeout=3)}.toOption match {
          case Some(response) => sender ! response
          case None =>
            connectParseServer()
        }
    }
  }

  class WebSocketWorker(val serverConnection: ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
    override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
    import org.fayalite.repl.REPL._
    import akka.pattern.ask

    def startHeartbeats() = {
      if (!started) {
        println("starting heartbeats")
        Future{
          Try{
            while (true) {
              Thread.sleep(10000)
              allSenders.foreach{
                case (sp, s) => s ! TextFrame("""{"flag": "heartbeat"}""")
            //      println("sent heartbeat")
              }
            }
          }
        }
      }
      started = true

    }

    def businessLogic: Receive = {
        case x@(_: BinaryFrame | _: TextFrame) =>


         //case TextFrame(msg)
          x match {
            case TextFrame(msg) =>
              val utfm = msg.utf8String
              println("textframe " + utfm)

              utfm match {
                case xs if xs == "reload" || xs == "init" =>
                  val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.10/fayalite-app-dynamic-fastopt.js")
                    .mkString
                  val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
                  val frame = TextFrame(msg)
                  //    Thread.sleep(3000)
                  sender() ! frame
                case _ => attemptParse(utfm, sender())
              }

            //  ImagePipe.parseMessage(utfm, sender)
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


          startHeartbeats()


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
        pathPrefix("css") {
          get {
            getFromResourceDirectory("css")
          }
        } ~
          pathPrefix("js") {
            get {
              getFromResourceDirectory("js")
            }
          } ~
        path("oauth_callback") {
       //   setCookie(HttpCookie("randomToken", Array.fill(10)(util.Random.nextInt(10).toString).mkString)) {
          get {
              getFromResource("oauth.html")
          }
    //    }
        } ~
          path("oauth_catch") {
            get {
              //          parameters('state, 'access_token, 'token_type, 'expires_in) { (state, access_token, token_type, expires_in) =>
              parameters('access_token) { (access_token) =>
                val authResponse =  oauth.OAuth.performGoogleOAuthRequest(access_token).getAsTry(10).printOpt
                setCookie(HttpCookie("access_token", access_token)) {
                  authResponse.map(oauth.OAuth.handleAuthResponse).foreach{
                    r => setCookie(HttpCookie("authResponse", "placeholder"))
                    }
                  getFromResource("index-fastopt.html")
                }
              }
            }
          } ~  //setCookie(HttpCookie("initToken", Array.fill(10)(util.Random.nextInt(10).toString).mkString)) {
          getFromResource("index-fastopt.html")}
      }
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
