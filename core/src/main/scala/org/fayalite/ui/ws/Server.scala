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
import spray.routing
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
  import spray.routing.directives._
  //{ oauth.OAuth.handleAuthResponse(q, access_token)

  var oauthCatch = (at: String, a: String) => ()

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
    //    org.fayalite.ui.ws.Heartbeat.startHeartbeats()(allSenders)
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
                parameters('access_token) { access_token =>
                import fa._
          oauth.OAuth.performGoogleOAuthRequest(access_token)
                  .onComplete{_.foreach{
                    q =>
                      //.getAsTry(10).printOpt


            parseServer.foreach { p => p !
                          oauth.OAuth.handleAuthResponse(q, access_token)
                        }

                  }}(ec)
                  getIndex
              }
            }
          } ~   pathPrefix("fayalite-app-dynamic") {
          get {
            //          println("pathPrefix(\"fayalite-app-dynamic\") {" +
            //            " " + Common.currentDir + "app-dynamic/target/scala-2.11/")
            val r1 = unmatchedPath {
              path =>
                import fa._
                //  println(path.toString() + " unmatched path")
                val fp = currentDir +
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
                getIndex
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
          getIndex
        }
      }
    }

    import fa._
    def getIndex: routing.Route = {
      getFromFile(currentDir + "index-fastopt.html")
    }
  }


  def doMain() {
    implicit val system = ActorSystem()

    val server = system.actorOf(WebSocketServer.props(), "websocket")

    IO(UHttp) ! Http.Bind(server, "0.0.0.0", 80)

    import fa._

    system.enterToShutdown()

  }

  doMain()
}