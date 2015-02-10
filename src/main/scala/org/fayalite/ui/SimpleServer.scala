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

    def businessLogic: Receive = {
      // just bounce frames back for Autobahn testsuite
   //   println("buslogic websoket.");
  //    {
        case x@(_: BinaryFrame | _: TextFrame) =>
       //   println("businessLogic run " + sender.path)
          x match {
            case TextFrame(msg) =>
              val umsg = msg.utf8String
        //      println("utf msg " + msg.utf8String)
            //  sender() ! TextFrame("msg reciv")
          //    sender() ! TextFrame(parser.??[String](msg.utf8String))
              scala.tools.nsc.io.File("msgio.txt").writeAll(umsg.split(",").mkString("\n"))

            case BinaryFrame(dat) =>
              SparkReference.getSC.makeRDD(Seq(dat)).saveAsObjectFile("dat")
             /*   val ab = dat.toArray.map{
                b => b & 0xFF;}
              import java.awt.image.BufferedImage
              val image = new BufferedImage(460, 495, BufferedImage.TYPE_INT_ARGB);
              val g = image.createGraphics()
              g.setBackground(Color.red)
              //g.drawImage(image, null, 0, 0);
              g.setColor(Color.white)
              g.drawString("yo", 50, 50) // (x/300, y/300)
            val w = image.getWidth
              val h = image.getHeight
              val rgba =     image.getRGB(0, 0, w, h, null, 0, w);
                image.setRGB(0, 0, w, h, ab, 0, w)
              //   val d3 = bi2db(image)
              //  d3
              val ri = image.asInstanceOf[RenderedImage]
              val fi = new java.io.File("/Users/ryle/adfsf.jpg")
              ImageIO.write(ri, "JPEG", fi)

                println(rgba.length)
                println(ab.length)*/
                println("binaryframe.")
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

//  val parser = org.fayalite.util.Tutils.parseClient()

  // because otherwise we get an ambiguous implicit if doMain is inlined
  doMain()
}
