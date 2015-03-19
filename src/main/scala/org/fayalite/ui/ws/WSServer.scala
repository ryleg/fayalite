package org.fayalite.ui.ws

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.ui.{MySslConfiguration, oauth}
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.util.{JSON, RemoteClient, SimpleRemoteServer}
import spray.can.server.UHttp
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import spray.can.{Http, websocket}
import spray.http.{DateTime, HttpCookie, HttpRequest}
import spray.routing.HttpServiceActor

import scala.concurrent.Future
import scala.io.Source
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

  def attemptParse(msg: String, sender: ActorRef) = {
    import org.fayalite.repl.REPL._

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
          case _ =>
            println("binary frame")
          //      allSenders(sender().path.toString) = sender()
        }
        allSenders(sender().path.toString) = sender()

        org.fayalite.ui.ws.Heartbeat.startHeartbeats()(allSenders)

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
            get {
              getFromResource("oauth.html")
            }
          } ~
          path("oauth_catch") {
            get {
              //            parameters('state, 'access_token.as[String], 'token_type, 'expires_in) {
              //              (state : String, access_token: String, token_type: String, expires_in : String) =>
              parameters('access_token) { access_token =>
                import org.fayalite.Fayalite._
                val authResponse = oauth.OAuth.performGoogleOAuthRequest(access_token).getAsTry(10).printOpt
                setCookie(
                  HttpCookie(
                    "access_token",
                    access_token,
                    expires=Some(DateTime(2020,1,1,1,1,1))
                    )) {
                  authResponse.map(ar => oauth.OAuth.handleAuthResponse(ar, access_token)).foreach {
                    r => parseServer.foreach{p => p ! r }
                  }
                  getFromResource("index-fastopt.html")
                }
              }
            }
          } ~
          getFromResource("index-fastopt.html")
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

  doMain()
}
