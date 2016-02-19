
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import spray.io._

// for SSL support (if enabled in application.conf)
/**
 * This came from some other example, I don't really know whats
 * going on in here. Don't touch it.
 */
trait MySslConfiguration {

  // if there is no SSLContext in scope implicitly the HttpServer uses the default SSLContext,
  // since we want non-default settings in this example we make a custom SSLContext available here
  implicit def sslContext: SSLContext = {
    val keyStoreResource = "/ssl-test-keystore.jks"
    val password = ""

    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(getClass.getResourceAsStream(keyStoreResource), password.toCharArray)
    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(keyStore, password.toCharArray)
    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    trustManagerFactory.init(keyStore)
    val context = SSLContext.getInstance("TLS")
    context.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)
    context
  }

  // if there is no ServerSSLEngineProvider in scope implicitly the HttpServer uses the default one,
  // since we want to explicitly enable cipher suites and protocols we make a custom ServerSSLEngineProvider
  // available here
  implicit def sslEngineProvider: ServerSSLEngineProvider = {
    ServerSSLEngineProvider { engine =>
      engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_256_CBC_SHA"))
      engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
      engine
    }
  }
}


import akka.actor._
import akka.io.IO
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{TextFrame, BinaryFrame}
import spray.can.{websocket, Http}
import spray.can.server.UHttp
import spray.http.HttpRequest
import spray.routing
import spray.routing.HttpServiceActor

import scala.reflect.ClassTag
import scala.util.{Success, Failure, Try}


val system = ActorSystem()

class WebSocketWorker2(val serverConnection: akka.actor.ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
  def businessLogic: Receive = {
    case TextFrame(xq) =>
      val socketMsgStr = xq.utf8String
    case x: FrameCommandFailed =>
      println("frame failed " + x)
    case x: HttpRequest =>
      println("httprequest " + x)
    case x => println("unrecognized frame" + x)
  }

  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: akka.actor.ActorRefFactory = context
    runRoute {
      getFromFile("index.html") // ~ put your rest API here after this tilda
    }
  }
}

// Fix this to polymorphic in the jar.
class WebSocketServer extends akka.actor.Actor with akka.actor.ActorLogging {
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val p = akka.actor.Props({new WebSocketWorker2(serverConnection)})
      val conn = context.actorOf(p)
      serverConnection ! Http.Register(conn)
  }
}
val server = system.actorOf(Props(new WebSocketServer()), "websocket")
IO(UHttp)(system) ! Http.Bind(server, "0.0.0.0", 80)

