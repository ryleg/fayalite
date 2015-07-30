package org.fayalite.ui.ws

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.io.IO
import org.fayalite.ui.{MySslConfiguration, oauth}
import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.util.{Common, JSON, RemoteClient, SimpleRemoteServer}
import spray.can.server.UHttp
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import spray.can.{Http, websocket}
import spray.http.{DateTime, HttpCookie, HttpRequest}
import spray.routing.HttpServiceActor

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

import ExecutionContext.Implicits.global

/**
 * Don't touch this it's just a stub from some example
 * extend the logic elsewhere and replace this later.
 * All this does in principle is host a websocket bridge that mediates
 * client browsers with other JVMs for handling the interpretation
 * of their requests.
 */
object Server extends App with MySslConfiguration {

  import scala.collection.mutable.{Map => MMap}
  type SenderMap = MMap[String, ActorRef]
  val allSenders = MMap[String, ActorRef]()


  type SprayFrame = spray.can.websocket.frame.Frame

  case class PipedMessage(senderPath: String, message: SprayFrame)

  case class RequestClients()

  val pipePort = defaultPort + 168
  val pipeServer = new SimpleRemoteServer({new Pipe(allSenders)}, pipePort)

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

    //println("attempting parse " + msg)
    parseServer match {
      case None => connectParseServer()
      case Some(ps) =>
        //    println("sending parse request")
        Try{ps.??[TextFrame](msg, timeout=3)}.toOption match {
          case Some(response) =>
            //   println("received parse response " + response)
            sender ! response
          case None =>
            connectParseServer()
        }
    }
  }

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
    def businessLogic: Receive = {
      case x@(_: BinaryFrame | _: TextFrame) =>
        x match {
          case TextFrame(msg) =>
            val utfm = msg.utf8String
            //      println("botlnck start")
            Future{Try{Bottleneck.rx(utfm, sender())}}
          //      println("botlnck end")
          case _ =>
            println("binary frame")
        }

        // Websockets will auto-terminate without this
        // hack. TODO : Cleanup / remove dead-letters / empty
        // senders
        allSenders(sender().path.toString) = sender()
      //  allSenders(sender().path.toString) = sender()
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
                // Put these in OAuth session from parseServer, return access token set as cookie
                val authResponse = oauth.OAuth.performGoogleOAuthRequest(access_token).getAsTry(10).printOpt
                setCookie(
                  HttpCookie(
                    "access_token",
                    access_token,
                    expires=Some(DateTime(2020,1,1,1,1,1))
                  )) {
                  authResponse.map(
                    ar => oauth.OAuth.handleAuthResponse(ar, access_token)).flatten.foreach {
                    r => parseServer.foreach{p => p ! r }
                  }
                  getFromFile(Common.currentDir + "index-fastopt.html")
                }
              }
            }
          } ~   pathPrefix("fayalite-app-dynamic") {
          get {
            //          println("pathPrefix(\"fayalite-app-dynamic\") {" +
            //            " " + Common.currentDir + "app-dynamic/target/scala-2.11/")
            val r1 = unmatchedPath {
              path =>
                //  println(path.toString() + " unmatched path")
                val fp = Common.currentDir +
                  "app-dynamic/target/scala-2.11/fayalite-app-dynamic" +
                  path.toString()
                //     println(fp)
                getFromFile(fp)
            }
            r1
            /*getFromDirectory(Common.currentDir +
              "app-dynamic/target/scala-2.11/fayalite-app-dynamic")*/
          }
        } ~   pathPrefix("template") {
          get {
            val r1 = unmatchedPath {
              path =>
                //  println(path.toString() + " unmatched path")
                val actualPath = path.toString()
                val pfx = actualPath.split("/").head
                //     println("pfx " + pfx)
                getFromFile(Common.currentDir + "index-fastopt.html")
              /* // TODO : Template by id.
                              val fp = Common.currentDir +
                                s"tmp/$pfx/target/scala-2.11/fayalite-app-template" +
                                path.toString()
                              println(fp)
                              getFromFile(fp)*/
            }
            r1
            /*getFromDirectory(Common.currentDir +
              "app-dynamic/target/scala-2.11/fayalite-app-dynamic")*/
          }
        }  ~ {
          //    println("file: ==" + Common.currentDir + "index-fastopt.html")
          getFromFile(Common.currentDir + "index-fastopt.html")
        }
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
